package org.secuso.privacyfriendlywerwolf.server;

import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.secuso.privacyfriendlywerwolf.activity.StartHostActivity;
import org.secuso.privacyfriendlywerwolf.context.GameContext;
import org.secuso.privacyfriendlywerwolf.controller.VotingController;
import org.secuso.privacyfriendlywerwolf.data.PlayerHolder;
import org.secuso.privacyfriendlywerwolf.model.Citizen;
import org.secuso.privacyfriendlywerwolf.model.Player;
import org.secuso.privacyfriendlywerwolf.util.Constants;
import org.secuso.privacyfriendlywerwolf.util.GameUtil;

import java.util.ArrayList;
import java.util.List;

import static org.secuso.privacyfriendlywerwolf.context.GameContext.activeRoles;
import static org.secuso.privacyfriendlywerwolf.util.Constants.START_GAME_;

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
        activeRoles = new ArrayList<>();
        serverHandler = new WebSocketServerHandler();
        serverHandler.setServerGameController(this);
        votingController = VotingController.getInstance();
    }

    public static ServerGameController getInstance() {
        return SERVER_GAME_CONTROLLER;

    }

    WebSocketServerHandler serverHandler;
    StartHostActivity startHostActivity;
    GameContext gameContext;
    VotingController votingController;


    public void initiateGame() {
        //TODO: send all the players, initiate time and so on
        //TODO: specify player roles
        //TODO: add playerRoles
        //TODO: send initial Time
        //
        Log.d(TAG, "Server send: start the Game!");
        String playerString = buildPlayerString();
        Log.d(TAG, "PlayerString:" + playerString);
        serverHandler.send(playerString);
        //TODO: sleep sometime just for now
        SystemClock.sleep(20000);
        initiateCitizenVoting();

    }

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
    }


    @NonNull
    private String buildPlayerString() {
        List<Player> players = PlayerHolder.getInstance().getPlayers();
        StringBuilder sb = new StringBuilder();
        sb.append(START_GAME_);
        for (Player player : players) {
            sb.append(player.getName());
            sb.append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public void startServer() {
        serverHandler.startServer();
    }


    public void sendTime() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("time", gameContext.getCurrentTime());
        serverHandler.send(json);
    }

    public void addPlayer(String playerName) {
        Player player = new Player();
        playerName = playerName.replace("playerName_", " ").trim();
        player.setName(playerName);
        player.setPlayerRoles(new Citizen());
        //TODO: add different roles randomized!
        PlayerHolder.getInstance().addPlayer(player);
        startHostActivity.addPlayer(playerName);
    }

    public void handleVotingResult(String playerName) {
        playerName = playerName.replace("votingResult_", " ").trim();
        Player player = PlayerHolder.getInstance().getPlayerByName(playerName);
        votingController.addVote(player);
        Log.d(TAG, "voting received for: "+ playerName);
        if(votingController.allVotesReceived()){
            Player winner = votingController.getVotingWinner();
            winner.setDead(true);
            Log.d(TAG, "all votes received kill this guy:"+ winner.getName());
            serverHandler.send("votingResult_"+ winner.getName());
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
        PlayerHolder.getInstance().setPlayers(new ArrayList<Player>());
        serverHandler.destroy();
    }
}
