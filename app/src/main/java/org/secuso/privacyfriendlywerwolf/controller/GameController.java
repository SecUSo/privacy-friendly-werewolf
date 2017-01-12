package org.secuso.privacyfriendlywerwolf.controller;

import org.secuso.privacyfriendlywerwolf.activity.StartClientActivity;

/**
 * Created by Tobi on 27.11.2016.
 */

public interface GameController {

    void startGame(String playerString);

    void connect(String url, String playerName);

    public void setStartClientActivity(StartClientActivity startClientActivity);
}
