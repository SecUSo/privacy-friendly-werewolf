package org.secuso.privacyfriendlywerwolf.controller;

import org.secuso.privacyfriendlywerwolf.activity.GameActivity;
import org.secuso.privacyfriendlywerwolf.activity.StartClientActivity;
import org.secuso.privacyfriendlywerwolf.model.Player;

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

    /**
     * initiates the voting on the view
     */
    public void startVoting();

    /**
     * sends the voting result to the server
     */
    public void sendVotingResult(Player player);

    /**
     * handles the result of the voting of all clients (e.g. kills a player)
     */
    public void handleVotingResult(String playerName);

    public void setGameActivity(GameActivity gameActivity);
}
