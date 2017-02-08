package org.secuso.privacyfriendlywerwolf.server;

import android.util.Log;

import org.secuso.privacyfriendlywerwolf.activity.GameActivity;
import org.secuso.privacyfriendlywerwolf.activity.StartHostActivity;
import org.secuso.privacyfriendlywerwolf.client.ClientGameController;
import org.secuso.privacyfriendlywerwolf.context.GameContext;
import org.secuso.privacyfriendlywerwolf.controller.Controller;
import org.secuso.privacyfriendlywerwolf.model.NetworkPackage;
import org.secuso.privacyfriendlywerwolf.model.Player;
import org.secuso.privacyfriendlywerwolf.util.Constants;
import org.secuso.privacyfriendlywerwolf.util.GameUtil;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

//import org.secuso.privacyfriendlywerwolf.activity.GameHostActivity;


/**
 * updates the model on the server, aswell as the view on the host and initiates communication to the clients
 *
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class ServerGameController extends Controller {
    //TODO: implements ServerGameController, rename to ..Impl -> use an interface!
    private static final String TAG = "ServerGameController";
    private static final ServerGameController SERVER_GAME_CONTROLLER = new ServerGameController();

    WebSocketServerHandler serverHandler;
    StartHostActivity startHostActivity;
    GameActivity gameActivity;
    GameContext gameContext;
    VotingController votingController;
    ClientGameController clientGameController;

    private ServerGameController() {
        Log.d(TAG, "ServerGameController singleton created");

        gameContext = GameContext.getInstance();

        serverHandler = new WebSocketServerHandler();
        serverHandler.setServerGameController(this);
        votingController = VotingController.getInstance();
        clientGameController = ClientGameController.getInstance();

    }

    public static ServerGameController getInstance() {
        return SERVER_GAME_CONTROLLER;

    }

    public void initiateGame() {

        // first we have to make sure, that all players are correctly initalized
        // TODO: make own method to set randomly the player roles

        List<Player> players = gameContext.getPlayersList();
        int total_amount = players.size();
        int werewolfs_amount = 2;
        int villagers_amount = total_amount - werewolfs_amount;

        // generate random numbers
        Random rng = new Random(); // Ideally just create one instance globally
        Set<Integer> generated = new LinkedHashSet<Integer>();
        while (generated.size() < total_amount)
        {
            Integer next = rng.nextInt(total_amount);
            generated.add(next);
        }

        // set the role
        for(int nr : generated) {

            // fill werewolfes as long as we still have some left over
            if(werewolfs_amount > 0) {
                players.get(nr).setPlayerRole(Player.Role.WEREWOLF);
                werewolfs_amount--;
            }
            // fill villagers as long as we still have some left over
            else if(villagers_amount > 0) {
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

        gameContext.setCurrentPhase(GameContext.Phase.GAME_START);


    }


    public GameContext.Phase startNextPhase() {
        Log.d(TAG, "Server send: start nextPhase!");
        // String phase = "";
        // TODO: add more roles
        // TODO: add more conditions, when specific roles are out of the game
        // TODO: use final constants for Strings (e.g. ROLE_WEREWOLF)

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

        // TODO: why do we have to return the phase here?
        return gameContext.getCurrentPhase();
    }

    public void startServer() {

        serverHandler.startServer();
    }


    public void addPlayer(Player player) {

        gameContext.addPlayer(player);
        startHostActivity.renderUI();

    }

    public void handleVotingResult(String playerName) {

        Player player = GameContext.getInstance().getPlayerByName(playerName);
        votingController.addVote(player);
        Log.d(TAG, "voting received for: "+ playerName);
        if(votingController.allVotesReceived()){
            Player winner = votingController.getVotingWinner();
            winner.setDead(true);
            Log.d(TAG, "all votes received kill this guy:"+ winner.getPlayerName());

            clientGameController.handleVotingResult(winner.getPlayerName());
            NetworkPackage np = null;
            try {
                np = new NetworkPackage(NetworkPackage.PACKAGE_TYPE.VOTING_RESULT);
                np.setOption("playerName", winner.getPlayerName());
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
                List<Player> werewolves = GameUtil.getAllLivingWerewolfes();
                votingController.startVoting(werewolves.size());
                clientGameController.initiateWerewolfVotingPhase();
                return GameContext.Phase.PHASE_WEREWOLF_VOTING;
            case PHASE_WEREWOLF_VOTING:
                clientGameController.endWerewolfPhase();
                return GameContext.Phase.PHASE_WEREWOLF_END;
            case PHASE_WEREWOLF_END:
                clientGameController.initiateWitchPhase();
                return GameContext.Phase.PHASE_WITCH;
            case PHASE_WITCH:
                clientGameController.initiateSeerPhase();
                return GameContext.Phase.PHASE_SEER;
            case PHASE_SEER:
                clientGameController.initiateDayPhase();
                return GameContext.Phase.PHASE_DAY_START;
            case PHASE_DAY_START:
                List<Player> citizens = GameUtil.getAllLivingCitizen();
                votingController.startVoting(citizens.size());
                clientGameController.initiateCitzenVotingPhase();
                return GameContext.Phase.PHASE_DAY_VOTING;

            case PHASE_DAY_VOTING:
                clientGameController.endDayPhase();
                return GameContext.Phase.PHASE_DAY_END;
            case PHASE_DAY_END:
                return GameContext.Phase.GAME_START;
            default:
                return GameContext.Phase.GAME_START;
        }

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
        serverHandler.destroy();
    }
    /*public GameHostActivity getGameHostActivity() {
        return gameHostActivity;
    }

    public void setGameHostActivity(GameHostActivity gameHostActivity) {
        this.gameHostActivity = gameHostActivity;
    }*/

}
