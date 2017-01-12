package org.secuso.privacyfriendlywerwolf.controller;

/**
 * Created by Tobi on 27.11.2016.
 */

public interface GameController {

    public void onClick();

    public void onClickWerwolf();

    void startGame();

    void connect(String url, String playerName);
}
