package org.secuso.privacyfriendlywerwolf.server;

import org.json.JSONException;
import org.json.JSONObject;
import org.secuso.privacyfriendlywerwolf.activity.StartHostActivity;
import org.secuso.privacyfriendlywerwolf.context.GameContext;

/**
 * updates the model on the server, aswell as the view on the host and initiates communication to the clients
 *
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class ServerGameController {


    WebSocketServerHandler serverHandler;
    StartHostActivity startHostActivity;
    GameContext gameContext;

    public void initiateGame() {
        gameContext = new GameContext();
    }


    public void sendTime() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("time", gameContext.getCurrentTime());
        serverHandler.send(json);
    }

    public void addPlayer(String playerName) {
        startHostActivity.addNewPlayer(playerName.replace("playerName_", " "));
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
}
