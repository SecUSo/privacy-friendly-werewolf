package org.secuso.privacyfriendlywerwolf.controller;

import android.util.Log;

import org.secuso.privacyfriendlywerwolf.activity.GameActivity;
import org.secuso.privacyfriendlywerwolf.activity.StartClientActivity;
import org.secuso.privacyfriendlywerwolf.client.WebsocketClientHandler;

import java.util.ArrayList;

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
    }

    public static GameController getInstance() {
        return GAME_CONTROLLER;

    }


    @Override
    public void onClick() {

    }

    @Override
    public void onClickWerwolf() {

    }

    public void startGame() {

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
