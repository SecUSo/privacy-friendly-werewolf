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
import org.secuso.privacyfriendlywerwolf.model.NetworkPackage;


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
                        Log.d(TAG, "Server hat einen Request geschickt! " + s);

                        Gson gson = new Gson();
                        NetworkPackage np = gson.fromJson(s, NetworkPackage.class);

                        switch (np.getType()) {
                            case UPDATE:
                                GameContext gcToUpdate = gson.fromJson(np.getPayload().toString(), GameContext.class);
                                GameContext.getInstance().copy(gcToUpdate);
                                break;
                            case START_GAME:
                                GameContext gcForStartGame = gson.fromJson(np.getPayload().toString(), GameContext.class);
                                gameController.startGame(gcForStartGame);
                                break;
                            case VOTING_RESULT:
                                String playerVotedForName = np.getOption("playerName");
                                gameController.handleVotingResult(playerVotedForName);
                                break;
                            case PHASE:

                                break;
                            case SERVER_HELLO:
                                try {
                                    NetworkPackage<String> resp = new NetworkPackage<String>(NetworkPackage.PACKAGE_TYPE.CLIENT_HELLO);
                                    resp.setPayload(playerName);
                                    webSocket.send(gson.toJson(resp));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                        }


                        /*if(s.startsWith(Constants.INITIATE_VOTING_)){
                            Log.d(TAG, "initiate voting string received! Start the Voting");
                            gameController.startVoting();
                        }*/
                        //TODO: implement more handling of server requests



                        // Werewolf's turn
                        if (s.startsWith("phase_")) {
                            Log.d(TAG, "nextPhase Request received! Start " + s);
                            if(s.contains("Werewolf")) {
                                if(s.contains("Start")) {
                                    Log.d(TAG, "WerewolfPhase starting");
                                    gameController.initiateWerewolfPhase();
                                } else if(s.contains("End")) {
                                    Log.d(TAG, "WerewolfPhase ending");
                                    gameController.endWerewolfPhase();
                                }
                            } else if (s.contains("Witch")) {
                                Log.d(TAG, "WitchPhase starting");
                                gameController.initiateWitchPhase();
                            } else if (s.contains("Seer")) {
                                Log.d(TAG, "SeerPhase starting");
                                gameController.initiateSeerPhase();
                            } else if (s.contains("Day")) {
                                if(s.contains("Start")) {
                                    Log.d(TAG, "DayPhase starting");
                                    gameController.initiateDayPhase();
                                } else if(s.contains("End")) {
                                    Log.d(TAG, "DayPhase ending");
                                    gameController.endDayPhase();
                                }
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

    /**
     * Clientside method to send data packages over the network
     * @param networkPackage
     */
    public void send(NetworkPackage networkPackage) {
            Gson gson = new Gson();
            String s = gson.toJson(networkPackage);
            socket.send(s);
    }

    public GameController getGameController() {
        return gameController;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

}
