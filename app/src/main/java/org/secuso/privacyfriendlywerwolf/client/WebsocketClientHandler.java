package org.secuso.privacyfriendlywerwolf.client;

import android.util.Log;

import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;


/**
 * handles communication of the client
 *
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class WebsocketClientHandler {

    private static final String TAG = "WebsocketClientHandler";

    public void startClient(String url) {
        Log.d(TAG, "Starting the client");

        AsyncHttpClient.getDefaultInstance().websocket(url, null, new AsyncHttpClient.WebSocketConnectCallback() {
            @Override
            public void onCompleted(Exception ex, WebSocket webSocket) {
                if (ex != null) {
                    ex.printStackTrace();
                    return;
                }
                webSocket.send("ClientString");
                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    public void onStringAvailable(String s) {
                        System.out.println("I got a string: " + s);
                    }
                });
                webSocket.setDataCallback(new DataCallback() {
                    public void onDataAvailable(DataEmitter emitter, ByteBufferList byteBufferList) {
                        System.out.println("I got some bytes!");
                        // note that this data has been read
                        byteBufferList.recycle();
                    }
                });
            }
        });
    }
}
