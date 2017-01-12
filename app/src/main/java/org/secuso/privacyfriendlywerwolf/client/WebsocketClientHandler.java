package org.secuso.privacyfriendlywerwolf.client;

import android.util.Log;

import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import org.secuso.privacyfriendlywerwolf.controller.GameController;


/**
 * handles communication of the client
 *
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class WebsocketClientHandler {



    private static final String TAG = "WebsocketClientHandler";
    protected GameController gameController;

    public void startClient(String url, String playerName) {
        Log.d(TAG, "Starting the client");

        AsyncHttpClient.getDefaultInstance().websocket(url, null, new AsyncHttpClient.WebSocketConnectCallback() {
            String playerName;

            private AsyncHttpClient.WebSocketConnectCallback init(String name) {
                playerName = name;
                return this;
            }

            @Override
            public void onCompleted(Exception ex, final WebSocket webSocket) {
                if (ex != null) {
                    ex.printStackTrace();
                    return;
                }
                //send the playerName
                //webSocket.send("ClientString");
                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    public void onStringAvailable(String s) {
                        if (s.startsWith("sendPlayerName_")) {
                            Log.d(TAG, "PlayerName:" + s);
                            webSocket.send("playerName_"+playerName);
                        }
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
        }.init(playerName));
    }
    public GameController getGameController() {
        return gameController;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

}
