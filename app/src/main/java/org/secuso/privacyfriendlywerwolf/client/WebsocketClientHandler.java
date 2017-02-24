package org.secuso.privacyfriendlywerwolf.client;

import android.text.TextUtils;
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

                        final Gson gson = new Gson();
                        final NetworkPackage np = gson.fromJson(s, NetworkPackage.class);


                        if(np.getType() == NetworkPackage.PACKAGE_TYPE.SERVER_HELLO) {
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
                        } else if(np.getType() == NetworkPackage.PACKAGE_TYPE.START_GAME) {
                            GameContext gcToStartGame = gson.fromJson(np.getPayload().toString(), GameContext.class);
                            gameController.startGame(gcToStartGame);
                            gameController.updateMe();
                        } else {

                            gameController.getGameActivity().runOnGameThread(new Runnable() {
                                @Override
                                public void run() {


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
                                            if (!TextUtils.isEmpty(playerVotedForName)) {
                                                Log.d(TAG, playerVotedForName + " got voted");
                                            } else {
                                                Log.d(TAG, "No player was voted");
                                            }
                                            gameController.handleVotingResult(playerVotedForName);
                                            break;
                                        case WITCH_RESULT_POISON:
                                            String poisenedPlayer = np.getOption("poisenedName");
                                            if (!TextUtils.isEmpty(poisenedPlayer)) {
                                                Log.d(TAG, poisenedPlayer + " got poisened by the Witch");
                                            } else {
                                                Log.d(TAG, "Witch did not use her poison elixir");
                                            }
                                            gameController.handleWitchPoisonResult(poisenedPlayer);
                                            break;
                                        case WITCH_RESULT_ELIXIR:
                                            String savedPlayer = np.getOption("savedName");
                                            if (!TextUtils.isEmpty(savedPlayer)) {
                                                Log.d(TAG, savedPlayer + " got saved by the Witch");
                                            } else {
                                                Log.d(TAG, "Witch did not use her healing elixir");
                                            }
                                            gameController.handleWitchElixirResult(savedPlayer);
                                            break;
                                        case PHASE:
                                            GameContext.Phase phase = gson.fromJson(np.getPayload().toString(), GameContext.Phase.class);
                                            Log.d(TAG, "Current phase is " + phase);
                                            gameController.setPhase(phase);
                                            switch (phase) {
                                                case PHASE_WEREWOLF_START:
                                                    Log.d(TAG, "Client: Starting WerewolfPhase");
                                                    gameController.initiateWerewolfPhase();
                                                    break;
                                                case PHASE_WEREWOLF_END:
                                                    Log.d(TAG, "Client: Ending WerewolfPhase");
                                                    gameController.endWerewolfPhase();
                                                    break;
                                                case PHASE_WITCH_ELIXIR:
                                                    Log.d(TAG, "Client: Starting WitchElixirPhase");
                                                    gameController.initiateWitchElixirPhase();
                                                    break;
                                                case PHASE_WITCH_POISON:
                                                    Log.d(TAG, "Client: Starting WitchPoisonPhase");
                                                    gameController.initiateWitchPoisonPhase();
                                                    break;
                                                case PHASE_SEER:
                                                    Log.d(TAG, "Client: Starting SeerPhase");
                                                    gameController.initiateSeerPhase();
                                                    break;
                                                case PHASE_DAY_START:
                                                    Log.d(TAG, "Client: Starting DayPhase");
                                                    gameController.initiateDayPhase();
                                                    break;
                                                case PHASE_DAY_END:
                                                    Log.d(TAG, "Client: Ending DayPhase");
                                                    gameController.endDayPhase();
                                                    break;
                                                case PHASE_DAY_VOTING:
                                                    Log.d(TAG, "Client: Starting DayVotingPhase");
                                                    gameController.initiateDayVotingPhase();
                                                    break;
                                                case PHASE_WEREWOLF_VOTING:
                                                    Log.d(TAG, "Client: Starting WerewolfVotingPhase");
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
                            }, 0);
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
            Log.d(TAG, "mit Phase " + gameController.getGameContext().getCurrentPhase());
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
