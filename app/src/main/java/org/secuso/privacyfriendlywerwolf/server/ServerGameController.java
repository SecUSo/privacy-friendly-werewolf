package org.secuso.privacyfriendlywerwolf.server;

import org.json.JSONException;
import org.json.JSONObject;
import org.secuso.privacyfriendlywerwolf.context.GameContext;

/**
 * updates the model on the server and initiates communication to the clients
 *
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class ServerGameController {


    WebSocketServerHandler serverHandler;
    GameContext gameContext;

    public void initiateGame() {
        gameContext = new GameContext();
    }


    public void sendTime() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("time", gameContext.getCurrentTime());
        serverHandler.send(json);
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
