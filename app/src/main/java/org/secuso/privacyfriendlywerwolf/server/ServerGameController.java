package org.secuso.privacyfriendlywerwolf.server;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.secuso.privacyfriendlywerwolf.activity.GameActivity;
import org.secuso.privacyfriendlywerwolf.activity.GameHostActivity;
import org.secuso.privacyfriendlywerwolf.activity.StartHostActivity;
import org.secuso.privacyfriendlywerwolf.context.GameContext;
import org.secuso.privacyfriendlywerwolf.data.PlayerHolder;
import org.secuso.privacyfriendlywerwolf.model.Player;

import java.util.ArrayList;
import java.util.List;
import static org.secuso.privacyfriendlywerwolf.context.GameContext.activeRoles;


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

        gameContext = GameContext.getInstance();

        serverHandler = new WebSocketServerHandler();
        serverHandler.setServerGameController(this);
    }

    public static ServerGameController getInstance() {
        return SERVER_GAME_CONTROLLER;

    }
    WebSocketServerHandler serverHandler;
    StartHostActivity startHostActivity;
    GameHostActivity gameHostActivity;
    GameContext gameContext;


    public void initiateGame() {
        //TODO: send all the players, initiate time and so on
        //TODO: specify player roles
        //TODO: add playerRoles
        //TODO: send initial Time
        //
        Log.d(TAG, "Server send: start the Game!");
        String playerString = buildPlayerString();
        Log.d(TAG, "PlayerString:"+ playerString);
        gameContext.setCurrentPhase(GameContext.GAME_START);
        serverHandler.send(playerString);
    }

    public String startNextPhase() {
        Log.d(TAG, "Server send: start nextPhase!");
        String phase = "";
        // TODO: add more roles
        // TODO: add more conditions, when specific roles are out of the game
        // TODO: use final constants for Strings (e.g. ROLE_WEREWOLF)
        switch(gameContext.getCurrentPhase()) {
                case GameContext.GAME_START:
                gameContext.setCurrentPhase(GameContext.PHASE_WEREWOLF);
                phase = "Werewolf";
                break;
            case GameContext.PHASE_WEREWOLF:
                gameContext.setCurrentPhase(GameContext.PHASE_WITCH);
                phase = "Witch";
                break;
            case GameContext.PHASE_WITCH:
                gameContext.setCurrentPhase(GameContext.PHASE_SEER);
                phase = "Seer";
                break;
            case GameContext.PHASE_SEER:
                gameContext.setCurrentPhase(GameContext.PHASE_DAY);
                phase= "Day";
                break;
            case GameContext.PHASE_DAY:
                gameContext.setCurrentPhase(GameContext.GAME_START);
                break;
            default:
                gameContext.setCurrentPhase(GameContext.GAME_START);
                break;
        }
        Log.d(TAG, "Upcoming Phase is " + phase);
        if(!TextUtils.isEmpty(phase))
        serverHandler.send("phase_" + phase);

        return phase;
    }

    @NonNull
    private String buildPlayerString() {
        List<Player> players = PlayerHolder.getInstance().getPlayers();
        StringBuilder sb = new StringBuilder();
        sb.append("startGame_");
        for(Player player : players){
            sb.append(player.getName());
            sb.append("&");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    public void startServer(){
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
        PlayerHolder.getInstance().addPlayer(player);
        startHostActivity.addPlayer(playerName);
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

    public GameHostActivity getGameHostActivity() {return gameHostActivity;}

    public void setGameHostActivity(GameHostActivity gameHostActivity) {
        this.gameHostActivity = gameHostActivity;
    }
}
