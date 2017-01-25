package org.secuso.privacyfriendlywerwolf.client;

import android.util.Log;

import com.google.gson.Gson;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import org.json.JSONException;
import org.json.JSONObject;
import org.secuso.privacyfriendlywerwolf.context.GameContext;
import org.secuso.privacyfriendlywerwolf.controller.GameController;
import org.secuso.privacyfriendlywerwolf.controller.GameControllerImpl;
import org.secuso.privacyfriendlywerwolf.util.Constants;


/**
 * handles communication of the client
 *
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class WebsocketClientHandler {

    WebSocket socket;

    private static final String TAG = "WebsocketClientHandler";
    protected GameController gameController = GameControllerImpl.getInstance();

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
                        Log.d(TAG, "Server hat einen Request geschickt!");

                        // Getting the GameContext and start the game
                        if(s.startsWith("{\"classID\":\"GameContext\"")) {

                            // set GameContext
                            Gson gson = new Gson();
                            gameController.setGameContext(gson.fromJson(s, GameContext.class));

                        }

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
                        /*if(s.startsWith(Constants.INITIATE_VOTING_)){
                            Log.d(TAG, "initiate voting string received! Start the Voting");
                            gameController.startVoting();
                        }*/

                        if(s.startsWith(Constants.VOTING_RESULT)){
                            Log.d(TAG, "handle voting string received! Handle the Voting results");
                            gameController.handleVotingResult(s);
                        }
                        //TODO: implement more handling of server requests

                        // Werewolf's turn
                        if (s.startsWith("phase_")) {
                            Log.d(TAG, "nextPhase Request received! Start " + s);
                            if(s.contains("Werewolf")) {
                                gameController.initiateWerewolfPhase();
                            } else if (s.contains("Witch")) {
                                gameController.initiateWitchPhase();
                            } else if (s.contains("Seer")) {
                                gameController.initiateSeerPhase();
                            } else if (s.contains("Day")) {
                                gameController.initiateDayPhase();
                            } else if (s.contains("Voting")) {
                                Log.d(TAG, "initiate voting string received! Start the Voting");
                                gameController.initiateVotingPhase();
                            }
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
        return gameController;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

}
