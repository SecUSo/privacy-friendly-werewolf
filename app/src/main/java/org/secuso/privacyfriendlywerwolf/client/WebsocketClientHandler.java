package org.secuso.privacyfriendlywerwolf.client;

import android.util.Log;

import com.google.gson.Gson;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import org.json.JSONException;
import org.json.JSONObject;
import org.secuso.privacyfriendlywerwolf.context.GameContext;
import org.secuso.privacyfriendlywerwolf.model.NetworkPackage;
import org.secuso.privacyfriendlywerwolf.model.Player;


/**
 * handles communication of the client
 *
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class WebsocketClientHandler {

    WebSocket socket;

    private static final String TAG = "WebsocketClientHandler";
    protected ClientGameController gameController = ClientGameController.getInstance();

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
                        // all communication handled over controller!
                        Log.d(TAG, "Client hat einen Request erhalten: " + s);

                        Gson gson = new Gson();
                        NetworkPackage np = gson.fromJson(s, NetworkPackage.class);


                        switch (np.getType()) {
                            case SERVER_HELLO:


                                Player player = gson.fromJson(np.getPayload().toString(), Player.class);
                                gameController.setMyId(player.getPlayerId());
                                gameController.showSuccesfulConnection();

                                try {
                                    NetworkPackage<Player> resp = new NetworkPackage<Player>(NetworkPackage.PACKAGE_TYPE.CLIENT_HELLO);
                                    player.setName(playerName);
                                    resp.setPayload(player);
                                    webSocket.send(gson.toJson(resp));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case UPDATE:
                                GameContext gcToUpdate = gson.fromJson(np.getPayload().toString(), GameContext.class);
                                //TODO: in Start_GAME the gameController does this
                                GameContext.getInstance().copy(gcToUpdate);
                                gameController.updateMe();
                                break;
                            case START_GAME:
                                GameContext gcToStartGame = gson.fromJson(np.getPayload().toString(), GameContext.class);
                                gameController.startGame(gcToStartGame);
                                gameController.updateMe();
                                break;
                            case VOTING_RESULT:
                                String playerVotedForName = np.getOption("playerName");
                                Log.d(TAG, playerVotedForName + " got voted");
                                gameController.handleVotingResult(playerVotedForName);
                                break;
                            case WITCH_RESULT_POISON:
                                String poisenedPlayer = np.getOption("poisenedName");
                                Log.d(TAG, poisenedPlayer + " got poisened by the Witch");
                                gameController.handleWitchPoisonResult(poisenedPlayer);
                                break;
                            case WITCH_RESULT_ELIXIR:
                                String savedPlayer = np.getOption("savedName");
                                Log.d(TAG, savedPlayer + " got saved by the Witch");
                                gameController.handleWitchElixirResult(savedPlayer);
                                break;
                            case PHASE:
                                GameContext.Phase phase = gson.fromJson(np.getPayload().toString(), GameContext.Phase.class);
                                Log.d(TAG, "Current phase is " + phase);
                                gameController.setPhase(phase);
                                switch(phase) {
                                    case PHASE_WEREWOLF_START:
                                        gameController.initiateWerewolfPhase();
                                        break;
                                    case PHASE_WEREWOLF_END:
                                        gameController.endWerewolfPhase();
                                        break;
                                    case PHASE_WITCH_ELIXIR:
                                        gameController.initiateWitchElixirPhase();
                                        break;
                                    case PHASE_WITCH_POISON:
                                        gameController.initiateWitchPoisonPhase();
                                        break;
                                    case PHASE_SEER:
                                        gameController.initiateSeerPhase();
                                        break;
                                    case PHASE_DAY_START:
                                        gameController.initiateDayPhase();
                                        break;
                                    case PHASE_DAY_END:
                                        gameController.endDayPhase();
                                        break;
                                    case PHASE_DAY_VOTING:
                                        gameController.initiateDayVotingPhase();
                                        break;
                                    case PHASE_WEREWOLF_VOTING:
                                        gameController.initiateWerewolfVotingPhase();
                                        break;
                                }
                                break;
                            case ABORT:
                                gameController.abortGame();
                                break;
                            default:
                                break;
                        }
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
            Log.d(TAG, "Client send: " + s);
            socket.send(s);
    }

    public ClientGameController getGameController() {
        return gameController;
    }

    public void setGameController(ClientGameController gameController) {
        this.gameController = gameController;
    }

    public void destroy() {
        if(socket != null)
            socket.close();
    }

}
