package org.secuso.privacyfriendlywerwolf.server;

import com.koushikdutta.async.http.WebSocket;

import org.json.JSONException;
import org.json.JSONObject;
import org.secuso.privacyfriendlywerwolf.context.GameContext;

import java.util.List;

/**
 * Created by Tobi on 11.01.2017.
 */

public class ServerGameController {

    List<WebSocket> sockets;
    GameContext gameContext;


    public ServerGameController(List<WebSocket> sockets) {
        this.sockets = sockets;
    }

    public void initiateGame() {
        gameContext = new GameContext();
    }


    public void sendTime() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("time", gameContext.getCurrentTime());
        for (WebSocket socket : sockets) {
            socket.send(json.toString(4));
        }
    }

}
