package org.secuso.privacyfriendlywerwolf.server;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.secuso.privacyfriendlywerwolf.activity.StartHostActivity;
import org.secuso.privacyfriendlywerwolf.context.GameContext;
import org.secuso.privacyfriendlywerwolf.data.PlayerHolder;
import org.secuso.privacyfriendlywerwolf.model.Player;

import java.util.ArrayList;

import static org.secuso.privacyfriendlywerwolf.context.GameContext.activeRoles;

/**
 * updates the model on the server, aswell as the view on the host and initiates communication to the clients
 *
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class ServerGameController {

    private static final String TAG = "ServerGameController";
    private static final ServerGameController SERVER_GAME_CONTROLLER = new ServerGameController();

    private ServerGameController() {
        Log.d(TAG, "ServerGameController singleton created");
        activeRoles = new ArrayList<>();
        serverHandler = new WebSocketServerHandler();
        serverHandler.setServerGameController(this);
    }

    public static ServerGameController getInstance() {
        return SERVER_GAME_CONTROLLER;

    }
    WebSocketServerHandler serverHandler;
    StartHostActivity startHostActivity;
    GameContext gameContext;


    public void initiateGame() {
        //TODO: send all the players, initiate time and so on
        Log.d(TAG, "Server send: start the Game!");
        serverHandler.send("startGame_");
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
        Player player= new Player();
        player.setName(playerName);
        PlayerHolder.getInstance().addPlayer(player);
        startHostActivity.addPlayer(playerName.replace("playerName_", " "));
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
}
