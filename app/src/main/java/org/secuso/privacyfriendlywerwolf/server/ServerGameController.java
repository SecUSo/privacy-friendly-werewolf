package org.secuso.privacyfriendlywerwolf.server;

import org.json.JSONException;
import org.json.JSONObject;
import org.secuso.privacyfriendlywerwolf.context.GameContext;

/**
 * Created by Tobi on 11.01.2017.
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
