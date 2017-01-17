package org.secuso.privacyfriendlywerwolf.client;

import android.util.Log;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import org.json.JSONException;
import org.json.JSONObject;
import org.secuso.privacyfriendlywerwolf.controller.GameController;


/**
 * handles communication of the client
 *
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class WebsocketClientHandler {

    WebSocket socket;

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
               socket = webSocket;
                if (ex != null) {
                    ex.printStackTrace();
                    return;
                }

                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    public void onStringAvailable(String s) {
                        //TODO: Incoming messages will be handled here -> enhance here for further communication
                        // all communication handled over controller!
                        System.out.println("I got a string: " + s);
                        //send playerName if server requested it
                        if (s.startsWith("sendPlayerName_")) {
                            Log.d(TAG, "PlayerName:" + s);
                            webSocket.send("playerName_"+playerName);
                        }
                        //start game, if server requested it
                        if (s.startsWith("startGame_")){
                            Log.d(TAG, "startGameString received! Start the Game");
                            gameController.startGame(s);
                        }
                        //TODO: implement more handling of server requests, all communication will be initated by the server


                    }
                });
            }
        }.init(playerName));
    }

    public void send(String message){
        socket.send(message);
    }

    public void send(JSONObject json) throws JSONException {
        socket.send(json.toString(4));
    }


    public GameController getGameController() {
        socket.send("hallo");
        return gameController;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

}
