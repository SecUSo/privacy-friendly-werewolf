package org.secuso.privacyfriendlywerwolf.server;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.activity.GameActivity;
import org.secuso.privacyfriendlywerwolf.activity.MainActivity;
import org.secuso.privacyfriendlywerwolf.activity.StartHostActivity;
import org.secuso.privacyfriendlywerwolf.client.ClientGameController;
import org.secuso.privacyfriendlywerwolf.context.GameContext;
import org.secuso.privacyfriendlywerwolf.enums.GamePhaseEnum;
import org.secuso.privacyfriendlywerwolf.enums.SettingsEnum;
import org.secuso.privacyfriendlywerwolf.model.NetworkPackage;
import org.secuso.privacyfriendlywerwolf.model.Player;
import org.secuso.privacyfriendlywerwolf.util.Constants;
import org.secuso.privacyfriendlywerwolf.util.ContextUtil;
import org.secuso.privacyfriendlywerwolf.util.GameUtil;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.secuso.privacyfriendlywerwolf.util.ContextUtil.duplicate_player_indicator;


/**
 * Updates the model on the server, as well as the view on the host and initiates communication to the clients
 * Keeps all game information in sync and controls the actions to be triggered
 *
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 * @author Florian
 */
public class ServerGameController {

    private static final String TAG = "ServerGameController";
    private static final ServerGameController SERVER_GAME_CONTROLLER = new ServerGameController();

    private WebSocketServerHandler serverHandler;
    private StartHostActivity startHostActivity;
    private GameActivity gameActivity;
    private GameContext gameContext;
    private VotingController votingController;
    private ClientGameController clientGameController;

    public static boolean HOST_IS_DONE = false;
    public static boolean CLIENTS_ARE_DONE = false;


    /**
     * Constructor to create a new ServerGameController Singleton
     */
    private ServerGameController() {
        Log.d(TAG, "ServerGameController singleton created");

        gameContext = GameContext.getInstance();

        serverHandler = new WebSocketServerHandler();
        serverHandler.setServerGameController(this);
        votingController = VotingController.getInstance();
        clientGameController = ClientGameController.getInstance();

    }

    /**
     * Return the ServerGameController Singleton Instance
     *
     * @return the ServerGameController instance
     */
    public static ServerGameController getInstance() {
        return SERVER_GAME_CONTROLLER;

    }

    /**
     * Do the main preparations to start a game.
     * Tasks are: distribute the characters randomly to the players, inform all clients about
     * the game settigs, start with the first game phase
     */
    public void initiateGame() {

        List<Player> players = gameContext.getPlayersList();
        int total_amount = players.size();

        int werewolfs_amount = getWerewolfSetting();
        int witch_amount = getWitchSetting();
        int seer_amount = getSeerSetting();
        int villagers_amount = total_amount - werewolfs_amount;

        // just for testing
        /*players.get(0).setPlayerRole(Player.Role.WITCH);
        if(players.size()>1)
            players.get(1).setPlayerRole(Player.Role.WEREWOLF);
        if(players.size()>2)
            players.get(2).setPlayerRole(Player.Role.WEREWOLF);
        if(players.size()>3)
            players.get(3).setPlayerRole(Player.Role.WEREWOLF);*/


        // generate random numbers
        Random rng = new Random(); // Ideally just create one instance globally
        Set<Integer> generated = new LinkedHashSet<>();
        while (generated.size() < total_amount) {
            Integer next = rng.nextInt(total_amount);
            generated.add(next);
        }

        // set the role
        for (int nr : generated) {

            // fill werewolfes as long as we still have some left over
            if (werewolfs_amount > 0) {
                players.get(nr).setPlayerRole(Player.Role.WEREWOLF);
                werewolfs_amount--;
            }
            // fill seer as long as there is a seer left over and one villager left
            else if (seer_amount > 0 && villagers_amount > 1) {
                players.get(nr).setPlayerRole(Player.Role.SEER);
                seer_amount--;
                villagers_amount--;
            }
            // fill witch as long as there is a witch left over and one villager left
            else if (witch_amount > 0 && villagers_amount > 0) {
                players.get(nr).setPlayerRole(Player.Role.WITCH);
                witch_amount--;
                villagers_amount--;
            }
            // fill villagers as long as we still have some left over
            else if (villagers_amount > 0) {
                players.get(nr).setPlayerRole(Player.Role.CITIZEN);
            }
        }


        //TODO: why see line 58: there is a get, now here is a set why ?
        // first set all the important information into the GameContext
        gameContext.setPlayers(players);

        try {
            NetworkPackage np = new NetworkPackage<GameContext>(NetworkPackage.PACKAGE_TYPE.START_GAME);
            np.setPayload(gameContext);
            serverHandler.send(np);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //
        Log.d(TAG, "Server send: start the Game!");

        gameContext.setCurrentPhase(GamePhaseEnum.GAME_START);


    }


    public void startNextPhase() {
        if (HOST_IS_DONE && CLIENTS_ARE_DONE) {
            // reset variables before next phase
            HOST_IS_DONE = false;
            CLIENTS_ARE_DONE = false;
            Log.d(TAG, "Server send: start nextPhase!");

            // go to the next phase
            GamePhaseEnum phase = gameContext.getCurrentPhase();
            gameContext.setCurrentPhase(nextPhase(phase));

            if (gameContext.getCurrentPhase() == GamePhaseEnum.PHASE_DAY_START) {
                Random rand = new Random();
                int index = rand.nextInt(2);
                ContextUtil.RANDOM_INDEX = index;

                try {
                    NetworkPackage np = new NetworkPackage<>(NetworkPackage.PACKAGE_TYPE.PHASE);
                    np.setPayload(gameContext.getCurrentPhase());
                    if (gameContext.getCurrentPhase() == GamePhaseEnum.PHASE_DAY_START) {
                        np.setOption("random number", String.valueOf(index));
                    }
                    Log.d(TAG, "send current phase: " + gameContext.getCurrentPhase());
                    serverHandler.send(np);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    NetworkPackage np = new NetworkPackage<>(NetworkPackage.PACKAGE_TYPE.PHASE);
                    np.setPayload(gameContext.getCurrentPhase());
                    Log.d(TAG, "send current phase: " + gameContext.getCurrentPhase());
                    serverHandler.send(np);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            // host functions
            switch (phase) {
                case GAME_START:
                    Log.d(TAG, "Server: Starting WerewolfPhase");
                    clientGameController.initiateWerewolfPhase();
                    break;
                case PHASE_WEREWOLF_START:
                    Log.d(TAG, "Server: Starting WerewolfVotingPhase");
                    clientGameController.initiateWerewolfVotingPhase();
                    break;
                case PHASE_WEREWOLF_VOTING:
                    Log.d(TAG, "Server: Ending WerewolfPhase");
                    clientGameController.endWerewolfPhase();
                    break;
                case PHASE_WEREWOLF_END:
                    Log.d(TAG, "Server: Starting WitchElixirPhase");
                    clientGameController.initiateWitchElixirPhase();
                    break;
                case PHASE_WITCH_ELIXIR:
                    Log.d(TAG, "Server: Starting WitchPoisonPhase");
                    clientGameController.initiateWitchPoisonPhase();
                    break;
                case PHASE_WITCH_POISON:
                    Log.d(TAG, "Server: Starting SeerPhase");
                    clientGameController.initiateSeerPhase();
                    break;
                case PHASE_SEER:
                    Log.d(TAG, "Server: Ending SeerPhase");
                    clientGameController.endSeerPhase();
                    break;
                case PHASE_SEER_END:
                    Log.d(TAG, "Server: Starting DayPhase");
                    clientGameController.initiateDayPhase();
                    break;
                case PHASE_DAY_START:
                    Log.d(TAG, "Server: Starting DayVotingPhase");
                    clientGameController.initiateDayVotingPhase();
                    break;
                case PHASE_DAY_VOTING:
                    Log.d(TAG, "Server: Ending DayPhase");
                    clientGameController.endDayPhase();
                    break;
                case PHASE_DAY_END:
                    break;
                default:
                    break;
            }


        } else {
            Log.d(TAG, "In Method NextPhase() - Sorry but the Host is not ready yet");
        }

    }

    public void startServer() {

        serverHandler.startServer();
    }


    public void addPlayer(Player player) {


        if (ContextUtil.isDuplicateName(player.getPlayerName())) {
            player.setName(player.getPlayerName() + "_" + duplicate_player_indicator++);
        }
        gameContext.addPlayer(player);
        startHostActivity.renderUI();

    }

    public void handleVotingResult(String playerName) {
        HOST_IS_DONE = true;
        if (!TextUtils.isEmpty(playerName)) {
            Player player = GameContext.getInstance().getPlayerByName(playerName);
            votingController.addVote(player);
            Log.d(TAG, "voting received for: " + playerName);

        } else {
            votingController.addVote(null);
        }
        if (votingController.allVotesReceived()) {
            Player winner = votingController.getVotingWinner();
            if (winner != null) {
                winner.setDead(true);
                ContextUtil.lastKilledPlayerID = winner.getPlayerId();
                gameContext.setSetting(SettingsEnum.KILLED_BY_WEREWOLF, String.valueOf(winner.getPlayerId()));
                Log.d(TAG, "all votes received kill this guy:" + winner.getPlayerName());


                NetworkPackage np = null;
                try {
                    np = new NetworkPackage(NetworkPackage.PACKAGE_TYPE.VOTING_RESULT);
                    np.setOption("playerName", winner.getPlayerName());
                    serverHandler.send(np);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                clientGameController.handleVotingResult(winner.getPlayerName());

            } else {
                try {
                    NetworkPackage np = new NetworkPackage(NetworkPackage.PACKAGE_TYPE.VOTING_RESULT);
                    np.setOption("playerName", Constants.EMPTY_VOTING_PLAYER);
                    serverHandler.send(np);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                clientGameController.handleVotingResult(Constants.EMPTY_VOTING_PLAYER);
            }
        }
    }

    public void handleWitchResultPoison(Long id) {
        gameActivity.outputMessage(R.string.message_witch_sleep);
        gameActivity.setMediaPlayer(MediaPlayer.create(gameActivity.getApplicationContext(), R.raw.witch_sleeps));
        gameActivity.getMediaPlayer().start();

        HOST_IS_DONE = true;
        if (id != null) {
            Player player = gameContext.getPlayerById(id);
            player.setDead(true);
            ContextUtil.lastKilledPlayerIDByWitch = player.getPlayerId();
            clientGameController.getGameContext().setSetting(SettingsEnum.WITCH_POISON, String.valueOf(id));
            try {
                NetworkPackage np = new NetworkPackage(NetworkPackage.PACKAGE_TYPE.WITCH_RESULT_POISON);
                np.setOption("poisenedName", player.getPlayerName());
                serverHandler.send(np);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                NetworkPackage np = new NetworkPackage(NetworkPackage.PACKAGE_TYPE.WITCH_RESULT_POISON);
                np.setOption("poisenedName", Constants.EMPTY_VOTING_PLAYER);
                serverHandler.send(np);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // give the witch 5 secs time to close eyes
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void handleWitchResultElixir(Long id) {
        HOST_IS_DONE = true;
        if (id != null) {
            Player player = gameContext.getPlayerById(id);
            if (ContextUtil.lastKilledPlayerID != Constants.NO_PLAYER_KILLED_THIS_ROUND && gameContext.getPlayerById(ContextUtil.lastKilledPlayerID).getPlayerName().equals(player.getPlayerName())) {
                player.setDead(false);
                // reset variable
                ContextUtil.lastKilledPlayerID = Constants.NO_PLAYER_KILLED_THIS_ROUND;
                clientGameController.getGameContext().setSetting(SettingsEnum.WITCH_ELIXIR, "used");
            }

            try {
                NetworkPackage np = new NetworkPackage(NetworkPackage.PACKAGE_TYPE.WITCH_RESULT_ELIXIR);
                np.setOption("savedName", player.getPlayerName());
                serverHandler.send(np);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                NetworkPackage np = new NetworkPackage(NetworkPackage.PACKAGE_TYPE.WITCH_RESULT_ELIXIR);
                np.setOption("savedName", Constants.EMPTY_VOTING_PLAYER);
                serverHandler.send(np);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method is important for the game flow, it return the next phase for a given phase
     *
     * @param currentPhase the current phase
     * @return the following phase
     */
    private GamePhaseEnum nextPhase(GamePhaseEnum currentPhase) {

        switch (currentPhase) {
            case GAME_START:
                //clientGameController.initiateWerewolfPhase();
                return GamePhaseEnum.PHASE_WEREWOLF_START;
            case PHASE_WEREWOLF_START:
                List<Player> livingWerewolves = GameUtil.getAllLivingWerewolfes();
                votingController.startVoting(livingWerewolves.size());
                //clientGameController.initiateWerewolfVotingPhase();
                return GamePhaseEnum.PHASE_WEREWOLF_VOTING;
            case PHASE_WEREWOLF_VOTING:
                //clientGameController.endWerewolfPhase();
                return GamePhaseEnum.PHASE_WEREWOLF_END;
            case PHASE_WEREWOLF_END:
                //clientGameController.initiateWitchElixirPhase();
                return GamePhaseEnum.PHASE_WITCH_ELIXIR;
            case PHASE_WITCH_ELIXIR:
                //clientGameController.initiateWitchPoisonPhase();
                return GamePhaseEnum.PHASE_WITCH_POISON;
            case PHASE_WITCH_POISON:
                //clientGameController.initiateSeerPhase();
                return GamePhaseEnum.PHASE_SEER;
            case PHASE_SEER:
                return GamePhaseEnum.PHASE_SEER_END;
            case PHASE_SEER_END:
                //clientGameController.initiateDayPhase();
                //gameActivity.getNextButton().setVisibility(View.VISIBLE);
                return GamePhaseEnum.PHASE_DAY_START;
            case PHASE_DAY_START:
                List<Player> livingPlayers = GameUtil.getAllLivingPlayers();
                votingController.startVoting(livingPlayers.size());
                //clientGameController.initiateDayVotingPhase();
                return GamePhaseEnum.PHASE_DAY_VOTING;
            case PHASE_DAY_VOTING:
                //clientGameController.endDayPhase();
                return GamePhaseEnum.PHASE_DAY_END;
            case PHASE_DAY_END:
                //gameActivity.getNextButton().setVisibility(View.VISIBLE);
                return GamePhaseEnum.GAME_START;
            default:
                return GamePhaseEnum.GAME_START;
        }

    }

    private int getWitchSetting() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.getContextOfApplication());
        boolean witchPresent = sharedPref.getBoolean(Constants.pref_witch_player, true);
        return witchPresent ? 1 : 0;
    }

    private int getSeerSetting() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.getContextOfApplication());
        boolean seerPresent = sharedPref.getBoolean(Constants.pref_seer_player, true);
        return seerPresent ? 1 : 0;
    }

    private int getWerewolfSetting() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.getContextOfApplication());
        return sharedPref.getInt(Constants.pref_werewolf_player, 1);
    }

    public void prepareServerPlayer(String playerName) {
        // generate Server Player
        Player myPlayer = new Player();
        myPlayer.setPlayerId(Constants.SERVER_PLAYER_ID);
        myPlayer.setName(playerName);
        addPlayer(myPlayer);
        clientGameController.setMyId(myPlayer.getPlayerId());
        clientGameController.setMe(myPlayer);
    }

    /**
     * Send a message to all client to abort the game and destroy the server.
     */
    public void abortGame() {

        // inform all clients about the game abortion
        NetworkPackage np = null;
        try {
            np = new NetworkPackage<>(NetworkPackage.PACKAGE_TYPE.ABORT);
            Log.d(TAG, "send abort the game to all players");
            serverHandler.send(np);
        } catch (Exception e) {
            e.printStackTrace();
        }

        destroy();
        clientGameController.abortGame();


        // go back to start screen
        //gameActivity.goToMainActivity();
    }

    public GameContext getGameContext() {
        return gameContext;
    }

    public void setGameContext(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    public WebSocketServerHandler getServerHandler() {
        return serverHandler;
    }

    public void setServerHandler(WebSocketServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    public StartHostActivity getStartHostActivity() {
        return startHostActivity;
    }

    public void setStartHostActivity(StartHostActivity startHostActivity) {
        this.startHostActivity = startHostActivity;
    }

    public GameActivity getGameActivity() {
        return gameActivity;
    }

    public void setGameActivity(GameActivity gameActivity) {
        this.gameActivity = gameActivity;
    }


    public void destroy() {
        GameContext.getInstance().setPlayers(new ArrayList<Player>());
        ContextUtil.duplicate_player_indicator = 2;
        serverHandler.destroy();
    }


}
