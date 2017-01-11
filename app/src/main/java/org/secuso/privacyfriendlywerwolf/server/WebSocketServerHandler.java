package org.secuso.privacyfriendlywerwolf.server;

import android.util.Log;

import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.secuso.privacyfriendlywerwolf.activity.StartHostActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tobi on 10.01.2017.
 */
public class WebSocketServerHandler {

    private List<WebSocket> _sockets;
    private AsyncHttpServer server;

    private StartHostActivity startHostActivity;
    private ServerGameController serverGameController;


    public void startServer() {
        server = new AsyncHttpServer();

        _sockets = new ArrayList<WebSocket>();

        //simple http tests
        server.get("/hello", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                response.send("Hello!!!");
            }
        });

        server.get("/tollerTest", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                response.send("das ist ein toller test");
            }
        });


        //websocket refinmnent

        server.websocket("/ws", new AsyncHttpServer.WebSocketRequestCallback() {

            @Override
            public void onConnected(final WebSocket webSocket, AsyncHttpServerRequest request) {
                _sockets.add(webSocket);
                webSocket.send("Welcome Client");
                //closing procedures
                webSocket.setClosedCallback(new CompletedCallback() {
                    @Override
                    public void onCompleted(Exception ex) {
                        try {
                            if (ex != null)
                                Log.e("WebSocket", "Error");
                        } finally {
                            _sockets.remove(webSocket);
                        }
                    }
                });
                //will get called when client sends a string message!
                //TODO: implement logic for json
                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    @Override
                    public void onStringAvailable(String s) {
                        Log.d("SERVERTAG", s);
                        startHostActivity.addNewPlayer(s);
                        webSocket.send(s);
                    }
                });
            }
        });

        // listen on port 5000
        server.listen(5000);
        // browsing http://localhost:5000 will return Hello!!!
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

    public StartHostActivity getStartHostActivity() {
        return startHostActivity;
    }

    public void setStartHostActivity(StartHostActivity startHostActivity) {
        this.startHostActivity = startHostActivity;
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
