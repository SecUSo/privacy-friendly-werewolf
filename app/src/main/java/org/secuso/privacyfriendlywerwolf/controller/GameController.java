package org.secuso.privacyfriendlywerwolf.controller;

import org.secuso.privacyfriendlywerwolf.activity.GameActivity;
import org.secuso.privacyfriendlywerwolf.activity.StartClientActivity;

/**
 * Created by Tobi on 27.11.2016.
 */

public interface GameController {

    void startGame(String playerString);

    void initiateWerewolfPhase();
    void initiateWitchPhase();
    void initiateSeerPhase();
    void initiateDayPhase();

    void connect(String url, String playerName);

    public void setStartClientActivity(StartClientActivity startClientActivity);

    public void setGameActivity(GameActivity gameActivity);
}
