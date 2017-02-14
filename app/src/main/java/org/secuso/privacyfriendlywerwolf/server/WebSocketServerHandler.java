package org.secuso.privacyfriendlywerwolf.server;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;

import org.secuso.privacyfriendlywerwolf.context.GameContext;
import org.secuso.privacyfriendlywerwolf.model.NetworkPackage;
import org.secuso.privacyfriendlywerwolf.model.Player;

import java.util.ArrayList;
import java.util.List;

import static org.secuso.privacyfriendlywerwolf.model.NetworkPackage.PACKAGE_TYPE.SERVER_HELLO;

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
    public static int requestCounter = 0;
    public static int votingCounter = 0;


    public void startServer() {
        Log.d(TAG, "Starting the server");

        server = new AsyncHttpServer();
        _sockets = new ArrayList<WebSocket>();

        //websocket refinmnent

        server.websocket("/ws", new AsyncHttpServer.WebSocketRequestCallback() {

            @Override
            public void onConnected(final WebSocket webSocket, AsyncHttpServerRequest request) {
                //initial communction on connection of client
                _sockets.add(webSocket);
                Log.d(TAG, "Count of websockets:"+ _sockets.size());
                //initate request for player name

                try {
                    Gson gson = new Gson();
                    NetworkPackage<Player> np = new NetworkPackage<Player>(SERVER_HELLO);
                    long id = Double.doubleToLongBits(Math.random());
                    Player player = new Player();
                    player.setPlayerId(id);
                    np.setPayload(player);
                    webSocket.send(gson.toJson(np));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //closing procedures
                webSocket.setClosedCallback(new CompletedCallback() {
                    @Override
                    public void onCompleted(Exception ex) {
                        Log.e(TAG, "Server: ich bin completed obwohl ich das noch gar nicht sein sollte");
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
                //TODO: implement logic for json
                //TODO: Incoming messages will be handled here -> enhance here for further communication
                // all communication handled over controller!
                //callbacks after client send a String
                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    @Override
                    public void onStringAvailable(String s) {
                        Log.d("SERVERTAG", s);
                        //TODO: implement handling for different incoming strings
                        //TODO: implement handling of voting

                        Gson gson = new Gson();
                        NetworkPackage networkPackage = gson.fromJson(s, NetworkPackage.class);

                        switch(networkPackage.getType()) {
                            case CLIENT_HELLO:
                                Player player = gson.fromJson(networkPackage.getPayload().toString(), Player.class);
                                serverGameController.addPlayer(player);
                                break;
                            case VOTING_RESULT:
                                Log.d(TAG, (++votingCounter) + ". Voting Request");
                                String votedForName = (String) networkPackage.getPayload();
                                if(!TextUtils.isEmpty(votedForName)) {
                                    serverGameController.handleVotingResult(votedForName);
                                } else {
                                    serverGameController.handleVotingResult("");
                                }
                                break;
                            case WITCH_RESULT_POISON:
                                //Log.d(TAG, "Received result by witch, which is ");
                                String poisonId = networkPackage.getOption(GameContext.Setting.WITCH_POISON.toString());
                                if(!TextUtils.isEmpty(poisonId)) {
                                    serverGameController.handleWitchResultPoison(Long.parseLong(poisonId));
                                } else {
                                    serverGameController.handleWitchResultPoison(null);
                                }
                                break;
                            case WITCH_RESULT_ELIXIR:
                                String elixirId = networkPackage.getOption(GameContext.Setting.WITCH_ELIXIR.toString());
                                if(!TextUtils.isEmpty(elixirId)) {
                                    serverGameController.handleWitchResultElixir(Long.parseLong(elixirId));
                                } else {
                                    serverGameController.handleWitchResultElixir(null);
                                }
                                break;
                            case DONE:
                                Log.d(TAG, s + " is done!, count is: " + ++requestCounter);
                                //requestCounter++;
                                if(requestCounter == _sockets.size()) {
                                    Log.d(TAG, s + " All " + _sockets.size() + " Players are done!");
                                    requestCounter = 0;
                                    if(ServerGameController.HOST_IS_DONE) {
                                        Log.d(TAG, "Everyone is done!!!");
                                        ServerGameController.HOST_IS_DONE = false;
                                        ServerGameController.CLIENTS_ARE_DONE = false;
                                        serverGameController.startNextPhase();
                                    } else {
                                        Log.d(TAG, "This should not happen!! The host should not be slower than the Clients");
                                        ServerGameController.CLIENTS_ARE_DONE = true;
                                    }
                                }
                                break;
                        }

                        //TODO: implement voting handling etc...
//                        if(s.startsWith("voting_")){
//                            //do smth.
//                        }

                    }
                });
            }
        });

        // listen on port 5000
        server.listen(5000);
    }

    /**
     * Message to send data packages over the network
     * @param networkPackage
     */
    public void send(NetworkPackage networkPackage) {
        Gson gson = new Gson();
        String s = gson.toJson(networkPackage);

        Log.d(TAG, "Server sent package to all clients: " + s);
        for (WebSocket socket : _sockets) {
            socket.send(s);
        }
    }


    public ServerGameController getServerGameController() {
        return serverGameController;
    }

    public AsyncHttpServer getServer() {
        return server;
    }

    public void setServer(AsyncHttpServer server) {
        this.server = server;
    }

    public List<WebSocket> get_sockets() {
        return _sockets;
    }

    public void set_sockets(List<WebSocket> _sockets) {
        this._sockets = _sockets;
    }

    public void setServerGameController(ServerGameController serverGameController) {
        this.serverGameController = serverGameController;
    }

    public void destroy() {
        if(server != null)
        server.stop();
    }
}
