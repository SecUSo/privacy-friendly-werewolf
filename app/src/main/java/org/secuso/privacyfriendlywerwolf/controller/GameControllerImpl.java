package org.secuso.privacyfriendlywerwolf.controller;

import android.util.Log;

import org.secuso.privacyfriendlywerwolf.activity.GameActivity;
import org.secuso.privacyfriendlywerwolf.activity.StartClientActivity;
import org.secuso.privacyfriendlywerwolf.client.WebsocketClientHandler;
import org.secuso.privacyfriendlywerwolf.model.Player;

import java.util.ArrayList;
import java.util.List;

import static org.secuso.privacyfriendlywerwolf.context.GameContext.activeRoles;

/**
 * updates the model on the client, aswell as the view on the client and initiates communication to the server
 *
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class GameControllerImpl extends Controller implements GameController{

    private static final String TAG = "GameControllerImpl";
    private static final GameControllerImpl GAME_CONTROLLER = new GameControllerImpl();

    StartClientActivity startClientActivity;
    GameActivity gameActivity;
    WebsocketClientHandler websocketClientHandler;

    private GameControllerImpl() {
        Log.d(TAG, "GameController singleton created");
        activeRoles = new ArrayList<>();
        websocketClientHandler = new WebsocketClientHandler();
        websocketClientHandler.setGameController(this);
    }

    public static GameController getInstance() {
        return GAME_CONTROLLER;

    }

    public void startGame(String playerString) {
        //TODO: extract the roles of the players and give it to the activity
        //TODO: extract every other information which were send by the server
        List<Player> players = extractPlayers(playerString);
        startClientActivity.startGame(players);
    }

    private List<Player> extractPlayers(String playerString){
        ArrayList<Player> players = new ArrayList<>();
        String cuttedPlayers = playerString.replace("startGame_", " ").trim();

        String[] playerArray = cuttedPlayers.split("&");
        for(String playerNameString : playerArray){
            Player p = new Player();
            p.setName(playerNameString);
            players.add(p);
        }

        return players;
    }

    public void connect(String url, String playerName){
        websocketClientHandler.startClient(url, playerName);
    }


    public GameActivity getGameActivity() {
        return gameActivity;
    }

    public void setGameActivity(GameActivity gameActivity) {
        this.gameActivity = gameActivity;
    }

    public StartClientActivity getStartClientActivity() {
        return startClientActivity;
    }

    public void setStartClientActivity(StartClientActivity startClientActivity) {
        this.startClientActivity = startClientActivity;
    }

    public WebsocketClientHandler getWebsocketClientHandler() {
        return websocketClientHandler;
    }

    public void setWebsocketClientHandler(WebsocketClientHandler websocketClientHandler) {
        this.websocketClientHandler = websocketClientHandler;
    }
}
