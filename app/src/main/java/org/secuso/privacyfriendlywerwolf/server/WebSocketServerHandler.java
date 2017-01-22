package org.secuso.privacyfriendlywerwolf.server;

import android.util.Log;

import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * handles communication of the server
 *
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class WebSocketServerHandler {
    private static final String TAG = "WebSocketServerHandler";
    private List<WebSocket> _sockets;
    private AsyncHttpServer server;

    private ServerGameController serverGameController;
    public static int requestCounter = 0;


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
                webSocket.send("sendPlayerName_");
                //closing procedures
                webSocket.setClosedCallback(new CompletedCallback() {
                    @Override
                    public void onCompleted(Exception ex) {
                        Log.e(TAG, "ich bin completed obwohl ich das noch gar nicht sein sollte");
                        try {
                            if (ex != null)
                                ex.printStackTrace();
                                Log.e("WebSocket", "Error");
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
                        if(s.startsWith("playerName_")){
                            serverGameController.addPlayer(s);

                        }
                        if(s.startsWith("next")) {
                            requestCounter++;
                            Log.d("SERVERTAG", "Requests erhalten: " + requestCounter + " von " + _sockets.size());
                            if(requestCounter == _sockets.size()) {
                                requestCounter = 0;
                                Log.d("SERVERTAG", "Alle Clients bereit für nächste Phase");
                                serverGameController.startNextPhase();
                            }
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

    public void send(JSONObject json) throws JSONException {
        for (WebSocket socket : _sockets) {
            socket.send(json.toString(4));
        }
    }

    public void send(String msg) {
        for (WebSocket socket : _sockets) {
            socket.send(msg);
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

}
