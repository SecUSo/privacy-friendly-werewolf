package org.secuso.privacyfriendlywerwolf.server;

import android.text.TextUtils;
import android.util.Log;

import org.secuso.privacyfriendlywerwolf.activity.GameHostActivity;
import org.secuso.privacyfriendlywerwolf.activity.StartHostActivity;
import org.secuso.privacyfriendlywerwolf.context.GameContext;
import org.secuso.privacyfriendlywerwolf.controller.VotingController;
import org.secuso.privacyfriendlywerwolf.model.NetworkPackage;
import org.secuso.privacyfriendlywerwolf.model.Player;
import org.secuso.privacyfriendlywerwolf.util.GameUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * updates the model on the server, aswell as the view on the host and initiates communication to the clients
 *
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class ServerGameController {
    //TODO: implements ServerGameController, rename to ..Impl -> use an interface!
    private static final String TAG = "ServerGameController";
    private static final ServerGameController SERVER_GAME_CONTROLLER = new ServerGameController();

    private ServerGameController() {
        Log.d(TAG, "ServerGameController singleton created");

        gameContext = GameContext.getInstance();

        serverHandler = new WebSocketServerHandler();
        serverHandler.setServerGameController(this);
        votingController = VotingController.getInstance();
    }

    public static ServerGameController getInstance() {
        return SERVER_GAME_CONTROLLER;

    }

    WebSocketServerHandler serverHandler;
    StartHostActivity startHostActivity;
    GameHostActivity gameHostActivity;
    GameContext gameContext;
    VotingController votingController;


    public void initiateGame() {
        //TODO: send all the players, initiate time and so on
        //TODO: specify player roles
        //TODO: add playerRoles
        //TODO: send initial Time

        // first we have to make sure, that all players are correctly initalized
        // TODO: make own method to set randomly the player roles

        List<Player> players = gameContext.getPlayersList();
        int total_amount = players.size();
        int werewolfs_amount = 1;
        int villagers_amount = total_amount - werewolfs_amount;

        // generate random numbers
        /*Random rng = new Random(); // Ideally just create one instance globally
        Set<Integer> generated = new LinkedHashSet<Integer>();
        while (generated.size() < total_amount)
        {
            Integer next = rng.nextInt(max) + 1;
            generated.add(next);
        }

        // set the role
        for(int nr : generated) {

            // fill werewolfes as long as we still have some left over
            if(werewolfs_amount > 0) {
                players.get(nr).setPlayerRoles(new Werewolf());
                werewolfs_amount--;
            }
            // fill villagers as long as we still have some left over
            else if(villagers_amount > 0) {
                players.get(nr).setPlayerRoles(new Citizen());
            }
        }*/

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

        gameContext.setCurrentPhase(GameContext.GAME_START);
        //TODO: sleep sometime just for now


    }

    /*
    public void initiateWerewolfVoting() {
        List<Player> werewolfes = GameUtil.getAllLivingWerewolfes();
        votingController.startVoting(werewolfes.size());
        //TODO: serverHandler needs to have map with Role -> connectedID
        serverHandler.send(Constants.INITIATE_VOTING_);
    }

    public void initiateCitizenVoting() {
        List<Player> citizens = GameUtil.getAllLivingCitizen();
        votingController.startVoting(citizens.size());
        //TODO: serverHandler needs to have map with Role -> connectedID
        serverHandler.send(Constants.INITIATE_VOTING_);
    }*/

    //
    public String startNextPhase() {
        Log.d(TAG, "Server send: start nextPhase!");
        String phase = "";
        // TODO: add more roles
        // TODO: add more conditions, when specific roles are out of the game
        // TODO: use final constants for Strings (e.g. ROLE_WEREWOLF)
        switch(gameContext.getCurrentPhase()) {
                case GameContext.GAME_START:
                gameContext.setCurrentPhase(GameContext.PHASE_WEREWOLF_START);
                phase = "Werewolf_Start";
                break;
            case GameContext.PHASE_WEREWOLF_START:
                gameContext.setCurrentPhase(GameContext.PHASE_WEREWOLF_VOTING);
                // TODO: ändern auf .getAllLivingWerewolfes wenn Funktionalität da ist
                List<Player> werewolves = GameUtil.getAllLivingCitizen();
                //List<Player> werewolves = GameUtil.getAllLivingWerewolfes();
                votingController.startVoting(werewolves.size());
                // TODO: eventuell muss hier zwischen Werwolf und Citizen voting unterschieden werden
                phase = "Voting";
                break;
            case GameContext.PHASE_WEREWOLF_VOTING:
                gameContext.setCurrentPhase(GameContext.PHASE_WEREWOLF_END);
                phase = "Werewolf_End";
                break;
            case GameContext.PHASE_WEREWOLF_END:
                gameContext.setCurrentPhase(GameContext.PHASE_WITCH);
                phase = "Witch";
                break;
            case GameContext.PHASE_WITCH:
                gameContext.setCurrentPhase(GameContext.PHASE_SEER);
                phase = "Seer";
                break;
            case GameContext.PHASE_SEER:
                gameContext.setCurrentPhase(GameContext.PHASE_DAY_START);
                phase= "Day_Start";
                break;
            case GameContext.PHASE_DAY_START:
                gameContext.setCurrentPhase(GameContext.PHASE_DAY_VOTING);
                List<Player> citizens = GameUtil.getAllLivingCitizen();
                //List<Player> werewolves = GameUtil.getAllLivingWerewolfes();
                votingController.startVoting(citizens.size());
                // TODO: eventuell muss hier zwischen Werwolf und Citizen voting unterschieden werden
                phase = "Voting";
                break;
            case GameContext.PHASE_DAY_VOTING:
                gameContext.setCurrentPhase(GameContext.PHASE_DAY_END);
                phase = "Day_End";
                break;
            case GameContext.PHASE_DAY_END:
                gameContext.setCurrentPhase(GameContext.GAME_START);
                break;
            default:
                gameContext.setCurrentPhase(GameContext.GAME_START);
                break;
        }
        Log.d(TAG, "Upcoming Phase is " + phase);
        if(!TextUtils.isEmpty(phase)) {

            try {
                NetworkPackage np = new NetworkPackage<Integer>(NetworkPackage.PACKAGE_TYPE.PHASE);
                np.setPayload(phase);
                serverHandler.send(np);
            } catch (Exception e) {
                e.printStackTrace();
            }



        }

        return phase;
    }

    public void startServer() {
        serverHandler.startServer();
    }


    public void addPlayer(String playerName) {

        gameContext.addPlayer(new Player(playerName));
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


    public void destroy() {
        GameContext.getInstance().setPlayers(new ArrayList<Player>());
        serverHandler.destroy();
    }
    public GameHostActivity getGameHostActivity() {
        return gameHostActivity;
    }

    public void setGameHostActivity(GameHostActivity gameHostActivity) {
        this.gameHostActivity = gameHostActivity;
    }
}
