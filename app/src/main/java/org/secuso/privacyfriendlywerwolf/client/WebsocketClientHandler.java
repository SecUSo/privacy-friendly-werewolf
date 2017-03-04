package org.secuso.privacyfriendlywerwolf.client;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.context.GameContext;
import org.secuso.privacyfriendlywerwolf.enums.GamePhaseEnum;
import org.secuso.privacyfriendlywerwolf.model.NetworkPackage;
import org.secuso.privacyfriendlywerwolf.model.Player;
import org.secuso.privacyfriendlywerwolf.util.ContextUtil;


/**
 * handles communication of the client. Receives messages from the client and handles them in the
 * callback. Can also initiate communication with the server.
 * <p>
 * Creates an own thread in the callback, therefore thread handling is done via handlers.
 *
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class WebsocketClientHandler {

    private WebSocket socket;

    private static final String TAG = "WebsocketClientHandler";
    private ClientGameController gameController = ClientGameController.getInstance();

    /**
     * starting the client and initates the first call to the server, by sending
     * the client's playerName with the URL from the player's input
     *
     * @param url,        the URL to connect to
     * @param playerName, the playerName to inform the server
     */
    public void startClient(String url, String playerName) {
        Log.d(TAG, "Starting the client");

        AsyncHttpClient.getDefaultInstance().websocket(url, null, new AsyncHttpClient.WebSocketConnectCallback() {
            String playerName;

            private AsyncHttpClient.WebSocketConnectCallback init(String name) {
                playerName = name;
                return this;
            }

            /**
             * start the communication on a successful connection
             * @param ex
             * @param webSocket
             */
            @Override
            public void onCompleted(Exception ex, final WebSocket webSocket)  {
                socket = webSocket;

                if (ex != null) {
                    Log.e(TAG, "Connection failure. Show on UI");
                    gameController.connectionFailed();
                    ex.printStackTrace();
                    return;
                }

                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    public void onStringAvailable(String s) {
                        // all communication handled over controller!
                        Log.d(TAG, "Client has received a request: " + s);

                        final Gson gson = new Gson();
                        final NetworkPackage np = gson.fromJson(s, NetworkPackage.class);

                        // SERVER_HELLO AND START_GAME do not run on the GameThread
                        if (np.getType() == NetworkPackage.PACKAGE_TYPE.SERVER_HELLO) {
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
                        } else if (np.getType() == NetworkPackage.PACKAGE_TYPE.START_GAME) {
                            GameContext gcToStartGame = gson.fromJson(np.getPayload().toString(), GameContext.class);
                            gameController.startGame(gcToStartGame);
                            gameController.updateMe();
                        } else if(np.getType() == NetworkPackage.PACKAGE_TYPE.ABORT) {
                            gameController.getGameActivity().showTextPopup(R.string.popup_title_abort, R.string.popup_text_abort_by_host);
                            gameController.getGameActivity().runOnGameThread(new Runnable() {
                                @Override
                                public void run() {
                                    gameController.abortGame();
                                }
                            }, 5000);
                        } else {
                            //all GameActivites will run on an own thread
                            gameController.getGameActivity().runOnGameThread(new Runnable() {
                                @Override
                                public void run() {

                                    //react to the different network packages of the server
                                    switch (np.getType()) {

                                        case UPDATE:
                                            GameContext gcToUpdate = gson.fromJson(np.getPayload().toString(), GameContext.class);
                                            //TODO: in Start_GAME the gameController does this, no effect, but check the duplicate
                                            GameContext.getInstance().copy(gcToUpdate);
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
                                            GamePhaseEnum phase = gson.fromJson(np.getPayload().toString(), GamePhaseEnum.class);
                                            Log.d(TAG, "Current phase is " + phase);
                                            gameController.setPhase(phase);
                                            //react to the different phases
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
                                                case PHASE_SEER_END:
                                                    Log.d(TAG, "Client: Ending SeerPhase");
                                                    gameController.endSeerPhase();
                                                    break;
                                                case PHASE_DAY_START:
                                                    Log.d(TAG, "Client: Starting DayPhase");
                                                    String randomNumberString = np.getOption("random number");
                                                    ContextUtil.RANDOM_INDEX = Integer.parseInt(randomNumberString);
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


    /**
     * Clientside method to send data packages over the network
     *
     * @param networkPackage
     */
    public void send(NetworkPackage networkPackage) {
        Gson gson = new Gson();
        String s = gson.toJson(networkPackage);
        Log.d(TAG, "Client send: " + s);
        Log.d(TAG, "mit Phase " + gameController.getGameContext().getCurrentPhase());
        socket.send(s);
    }


    public void setGameController(ClientGameController gameController) {
        this.gameController = gameController;
    }

    /**
     * destroy the socket of the handler
     */
    public void destroy() {
        if (socket != null)
            socket.close();
    }

}
