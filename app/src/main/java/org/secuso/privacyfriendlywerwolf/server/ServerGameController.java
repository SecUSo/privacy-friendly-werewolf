package org.secuso.privacyfriendlywerwolf.server;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.activity.GameActivity;
import org.secuso.privacyfriendlywerwolf.activity.MainActivity;
import org.secuso.privacyfriendlywerwolf.activity.StartHostActivity;
import org.secuso.privacyfriendlywerwolf.client.ClientGameController;
import org.secuso.privacyfriendlywerwolf.context.GameContext;
import org.secuso.privacyfriendlywerwolf.controller.Controller;
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

import static org.secuso.privacyfriendlywerwolf.util.Constants.EMPTY_VOTING_PLAYER;
import static org.secuso.privacyfriendlywerwolf.util.ContextUtil.duplicate_player_indicator;


/**
 * Updates the model on the server, as well as the view on the host and initiates communication to the clients
 * Keeps all game information in sync and controls the actions to be triggered
 *
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 * @author Florian
 */
public class ServerGameController extends Controller {

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
        int villagers_amount = total_amount - werewolfs_amount - witch_amount - seer_amount;

        // just for testing
        players.get(0).setPlayerRole(Player.Role.WEREWOLF);
        if(players.size()>1)
            players.get(1).setPlayerRole(Player.Role.WITCH);
        if(players.size()>2)
            players.get(2).setPlayerRole(Player.Role.SEER);
        if(players.size()>3)
            players.get(3).setPlayerRole(Player.Role.WEREWOLF);


        /*
        // generate random numbers
        Random rng = new Random(); // Ideally just create one instance globally
        Set<Integer> generated = new LinkedHashSet<>();
        while (generated.size() < total_amount)
        {
            Integer next = rng.nextInt(total_amount);
            generated.add(next);
        }

        // set the role
        for (int nr : generated) {

            // fill werewolfes as long as we still have some left over
            if(werewolfs_amount > 0) {
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
        */

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

        gameContext.setCurrentPhase(GameContext.Phase.GAME_START);


    }


    public void startNextPhase() {
        if(HOST_IS_DONE && CLIENTS_ARE_DONE) {
            // reset variables before next phase
            HOST_IS_DONE = false;
            CLIENTS_ARE_DONE = false;
            Log.d(TAG, "Server send: start nextPhase!");

            // go to the next phase
            GameContext.Phase phase = gameContext.getCurrentPhase();
            gameContext.setCurrentPhase(nextPhase(phase));


            try {
                NetworkPackage np = new NetworkPackage<GameContext.Phase>(NetworkPackage.PACKAGE_TYPE.PHASE);
                np.setPayload(gameContext.getCurrentPhase());
                Log.d(TAG, "send current phase: " + gameContext.getCurrentPhase());
                serverHandler.send(np);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "In Method NextPhase() - Sorry but the Host is not ready yet");
        }

    }

    public void startServer() {

        serverHandler.startServer();
    }


    public void addPlayer(Player player) {


       if( ContextUtil.isDuplicateName(player.getPlayerName())){
           player.setName(player.getPlayerName() + duplicate_player_indicator++);
       }
        gameContext.addPlayer(player);
        startHostActivity.renderUI();

    }

    public void handleVotingResult(String playerName) {
        HOST_IS_DONE = true;
        if(!TextUtils.isEmpty(playerName)) {
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
                gameContext.setSetting(GameContext.Setting.KILLED_BY_WEREWOLF, String.valueOf(winner.getPlayerId()));
                Log.d(TAG, "all votes received kill this guy:" + winner.getPlayerName());

                clientGameController.handleVotingResult(winner.getPlayerName());
                NetworkPackage np = null;
                try {
                    np = new NetworkPackage(NetworkPackage.PACKAGE_TYPE.VOTING_RESULT);
                    np.setOption("playerName", winner.getPlayerName());
                    serverHandler.send(np);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    NetworkPackage np = new NetworkPackage(NetworkPackage.PACKAGE_TYPE.VOTING_RESULT);
                    np.setOption("playerName", "");
                    serverHandler.send(np);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void handleWitchResultPoison(Long id) {
        gameActivity.setMediaPlayer(MediaPlayer.create(gameActivity.getApplicationContext(), R.raw.witch_sleeps));
        gameActivity.getMediaPlayer().start();
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }
        HOST_IS_DONE = true;
        if(id!=null) {
            Player player = gameContext.getPlayerById(id);
            player.setDead(true);
            ContextUtil.lastKilledPlayerIDByWitch = player.getPlayerId();
            clientGameController.getGameContext().setSetting(GameContext.Setting.WITCH_POISON, String.valueOf(id));
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
                np.setOption("poisenedName", "");
                serverHandler.send(np);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void handleWitchResultElixir(Long id) {
        HOST_IS_DONE = true;
        if(id!=null) {
            Player player = gameContext.getPlayerById(id);
            if (gameContext.getSetting(GameContext.Setting.KILLED_BY_WEREWOLF).equals(String.valueOf(player.getPlayerId()))) {
                player.setDead(false);
                ContextUtil.lastKilledPlayerID = -1;
                clientGameController.getGameContext().setSetting(GameContext.Setting.WITCH_ELIXIR, "used");
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
                np.setOption("savedName", "");
                serverHandler.send(np);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method is important for the game flow, it return the next phase for a given phase
     * @param currentPhase the current phase
     * @return the following phase
     */
    private GameContext.Phase nextPhase(GameContext.Phase currentPhase) {

        switch(currentPhase) {
            case GAME_START:
                clientGameController.initiateWerewolfPhase();
                return GameContext.Phase.PHASE_WEREWOLF_START;
            case PHASE_WEREWOLF_START:
                List<Player> livingWerewolves = GameUtil.getAllLivingWerewolfes();
                votingController.startVoting(livingWerewolves.size());
                clientGameController.initiateWerewolfVotingPhase();
                return GameContext.Phase.PHASE_WEREWOLF_VOTING;
            case PHASE_WEREWOLF_VOTING:
                clientGameController.endWerewolfPhase();
                return GameContext.Phase.PHASE_WEREWOLF_END;
            case PHASE_WEREWOLF_END:
                clientGameController.initiateWitchElixirPhase();
                return GameContext.Phase.PHASE_WITCH_ELIXIR;
            case PHASE_WITCH_ELIXIR:
                clientGameController.initiateWitchPoisonPhase();
                return GameContext.Phase.PHASE_WITCH_POISON;
            case PHASE_WITCH_POISON:
                clientGameController.initiateSeerPhase();
                return GameContext.Phase.PHASE_SEER;
            case PHASE_SEER:
                clientGameController.initiateDayPhase();
                //gameActivity.getNextButton().setVisibility(View.VISIBLE);
                return GameContext.Phase.PHASE_DAY_START;
            case PHASE_DAY_START:
                List<Player> livingPlayers = GameUtil.getAllLivingPlayers();
                votingController.startVoting(livingPlayers.size());
                clientGameController.initiateDayVotingPhase();
                return GameContext.Phase.PHASE_DAY_VOTING;
            case PHASE_DAY_VOTING:
                clientGameController.endDayPhase();
                return GameContext.Phase.PHASE_DAY_END;
            case PHASE_DAY_END:
                //gameActivity.getNextButton().setVisibility(View.VISIBLE);
                return GameContext.Phase.GAME_START;
            default:
                return GameContext.Phase.GAME_START;
        }

    }

    private int getWitchSetting(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.getContextOfApplication());
        boolean witchPresent = sharedPref.getBoolean("pref_witch_player",true);
        return witchPresent ? 1 : 0;
    }

    private int getSeerSetting(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.getContextOfApplication());
        boolean seerPresent = sharedPref.getBoolean("pref_seer_player",true);
        return seerPresent ? 1 : 0;
    }

    private int getWerewolfSetting(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.getContextOfApplication());
        return  sharedPref.getInt("pref_werewolf_player", 1);
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

        // go back to start screen
        Intent intent = new Intent(gameActivity, MainActivity.class);
        gameActivity.startActivity(intent);
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

    public GameActivity getGameHostActivity() {
        return gameActivity;
    }

    public void setGameActivity(GameActivity gameActivity) {
        this.gameActivity = gameActivity;
    }


    public void destroy() {
        GameContext.getInstance().setPlayers(new ArrayList<Player>());
        ContextUtil.duplicate_player_indicator = 0;
        serverHandler.destroy();
    }


}
