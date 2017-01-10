package org.secuso.privacyfriendlywerwolf.server;

import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tobi on 10.01.2017.
 */
public class WebSocketServer {


       public void startServer() {
        AsyncHttpServer server = new AsyncHttpServer();

        List<WebSocket> _sockets = new ArrayList<WebSocket>();

        server.get("/", new HttpServerRequestCallback() {
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

           server.websocket("/ws", new AsyncHttpServer.WebSocketRequestCallback() {
               @Override
               public void onConnected(final WebSocket webSocket, AsyncHttpServerRequest request) {
                   webSocket.setStringCallback(new WebSocket.StringCallback() {
                       @Override
                       public void onStringAvailable(String s) {
                           webSocket.send(s);
                       }
                   });
               }
           });

        // listen on port 5000
        server.listen(5000);
        // browsing http://localhost:5000 will return Hello!!!
    }

}
