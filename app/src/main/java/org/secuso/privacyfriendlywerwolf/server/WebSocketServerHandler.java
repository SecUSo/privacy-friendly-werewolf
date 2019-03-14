package org.secuso.privacyfriendlywerwolf.server;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.koushikdutta.async.AsyncSSLSocketWrapper;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;

import org.secuso.privacyfriendlywerwolf.enums.SettingsEnum;
import org.secuso.privacyfriendlywerwolf.model.NetworkPackage;
import org.secuso.privacyfriendlywerwolf.model.Player;

import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static org.secuso.privacyfriendlywerwolf.model.NetworkPackage.PACKAGE_TYPE.SERVER_HELLO;
import static org.secuso.privacyfriendlywerwolf.util.Constants.EMPTY_VOTING_PLAYER;

/**
 * handles communication of the server
 *
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class WebSocketServerHandler {
    private static final String TAG = "WebSocketServerHandler";
    private List<WebSocket> _sockets;
    private AsyncHttpServer server;

    private ServerGameController serverGameController = ServerGameController.getInstance();
    private static int requestCounter = 0;
    private static int votingCounter = 0;

    /**
     * starts the websocket server and initiates the string callbacks to enable communication with
     * the client.
     */
    public void startServer() {
        Log.d(TAG, "Starting the server");

        server = new AsyncHttpServer();
        _sockets = new ArrayList<WebSocket>();

        //websocket refinmnent

        server.websocket("/ws", new AsyncHttpServer.WebSocketRequestCallback() {

            @Override
            public void onConnected(final WebSocket webSocket, AsyncHttpServerRequest request) {
                //initial communication on connection of client
                _sockets.add(webSocket);
                Log.d(TAG, "Count of websockets:" + _sockets.size());
                //initiate request for player name

                try {
                    final Gson gson = new GsonBuilder()
                            .setLenient()
                            .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                            .create();
                    NetworkPackage<Player> np = new NetworkPackage<Player>(SERVER_HELLO);
                    long id = Double.doubleToLongBits(Math.random());
                    Player player = new Player();
                    player.setPlayerId(id);
                    np.setPayload(player);
                    webSocket.send(gson.toJson(np).trim());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //closing procedures
                webSocket.setClosedCallback(new CompletedCallback() {
                    @Override
                    public void onCompleted(Exception ex) {
                        Log.e(TAG, "Server: i'm completed, even though i shouldnt be.");
                        try {
                            if (ex != null) {
                                ex.printStackTrace();
                                Log.d("WebSocket", "Error: " + ex.getMessage());
                            }
                        } finally {
                            _sockets.remove(webSocket);
                        }
                    }
                });
                //will get called when client sends a string message!
                //all communication handled over controller!
                //this are callbacks after client send a String
                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    @Override
                    public void onStringAvailable(String s) {
                        final Gson gson = new GsonBuilder()
                                .setLenient()
                                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                                .create();
                        String npString;
                        try {
                            npString = URLDecoder.decode(s, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            npString = s;
                        }

                        JsonReader networkPackageReader = new JsonReader(new StringReader(npString));
                        networkPackageReader.setLenient(true);
                        final NetworkPackage networkPackage = gson.fromJson(networkPackageReader, NetworkPackage.class);

                        // CLIENT_HELLO does not run on the GameThread
                        if (networkPackage.getType() == NetworkPackage.PACKAGE_TYPE.CLIENT_HELLO) {
                            String playerString;
                            try {
                                playerString = URLDecoder.decode(networkPackage.getPayload().toString(), "UTF-8").replaceAll(" ", "");;
                            } catch (UnsupportedEncodingException e) {
                                playerString = networkPackage.getPayload().toString().replaceAll(" ", "");;
                            }

                            JsonReader playerReader = new JsonReader(new StringReader(playerString));
                            playerReader.setLenient(true);
                            Player player = gson.fromJson(playerReader, Player.class);
                            serverGameController.addPlayer(player);
                            // TODO after Release: implement SERVER ACK
                        } else {
                            // post game logic onto the GameThread
                            serverGameController.getGameActivity().runOnGameThread(new Runnable() {
                                @Override
                                public void run() {

                                    switch (networkPackage.getType()) {
                                        case VOTING_RESULT:
                                            Log.d(TAG, (++votingCounter) + ". Voting Request");
                                            String votedForName = (String) networkPackage.getPayload();
                                            if (!TextUtils.isEmpty(votedForName)) {
                                                serverGameController.handleVotingResult(votedForName);
                                            } else {
                                                serverGameController.handleVotingResult(EMPTY_VOTING_PLAYER);
                                            }
                                            break;
                                        case WITCH_RESULT_POISON:
                                            //Log.d(TAG, "Received result by witch, which is ");
                                            String poisonId = networkPackage.getOption(SettingsEnum.WITCH_POISON.toString());
                                            if (!TextUtils.isEmpty(poisonId)) {
                                                serverGameController.handleWitchResultPoison(Long.parseLong(poisonId));
                                            } else {
                                                serverGameController.handleWitchResultPoison(null);
                                            }
                                            break;
                                        case WITCH_RESULT_ELIXIR:
                                            String elixirId = networkPackage.getOption(SettingsEnum.WITCH_ELIXIR.toString());
                                            if (!TextUtils.isEmpty(elixirId)) {
                                                serverGameController.handleWitchResultElixir(Long.parseLong(elixirId));
                                            } else {
                                                serverGameController.handleWitchResultElixir(null);
                                            }
                                            break;
                                        case DONE:
                                            Log.d(TAG, "Another Client is done! in Phase " + serverGameController.getGameContext().getCurrentPhase() + ", count is: " + ++requestCounter);
                                            //requestCounter++;
                                            if (requestCounter == _sockets.size()) {
                                                Log.d(TAG, "All " + _sockets.size() + " Players are done!");
                                                requestCounter = 0;
                                                if (ServerGameController.HOST_IS_DONE) {
                                                    ServerGameController.CLIENTS_ARE_DONE = true;
                                                    Log.d(TAG, "Everyone is done!!!");
                                                    Log.d(TAG, "in Phase " + serverGameController.getGameContext().getCurrentPhase());
                                                    serverGameController.startNextPhase();
                                                } else {
                                                    Log.d(TAG, "The Clients are waiting for the Host (why you so slow ._.)");
                                                    ServerGameController.CLIENTS_ARE_DONE = true;
                                                }
                                            }
                                            break;
                                    }
                                }
                            }, 0);
                        }
                    }
                });
            }
        });

        // listen on port 5000
        server.listen(5000);
    }

    /**
     * Message to send data packages over the network
     *
     * @param networkPackage
     */
    public void send(NetworkPackage networkPackage) {
        final Gson gson = new GsonBuilder()
                .setLenient()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .create();
        String s = gson.toJson(networkPackage);

        Log.d(TAG, "Server sent package to all clients: " + s);
        for (WebSocket socket : _sockets) {
            socket.send(s);
        }
    }


    public AsyncHttpServer getServer() {
        return server;
    }

    public void setServer(AsyncHttpServer server) {
        this.server = server;
    }

    public void setServerGameController(ServerGameController serverGameController) {
        this.serverGameController = serverGameController;
    }

    /**
     * kill the server process
     */
    public void destroy() {
        requestCounter = 0;
        votingCounter = 0;

        if (server != null)
            server.stop();
    }
}
