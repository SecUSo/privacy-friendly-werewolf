package org.secuso.privacyfriendlywerwolf.context;

import android.util.Log;

import org.secuso.privacyfriendlywerwolf.model.Player;
import org.secuso.privacyfriendlywerwolf.model.PlayerRole;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * singleton, which holds the players of the game
 *
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class GameContext  {

    private String classID = "GameContext";
    private ArrayList<Player> players = new ArrayList<Player>();

    private static final String TAG = "PlayerHolder";
    private static final GameContext GAME_CONTEXT = new GameContext();

    private GameContext() {
        Log.d(TAG, "PlayerHolder singleton created");
        activeRoles = new ArrayList<>();
    }

    public static GameContext getInstance() {
        return GAME_CONTEXT;
    }

    public static List<PlayerRole> activeRoles;

    //TODO: think correct type
    public static Timestamp roundTime;

    //TODO: think correct type
    public static int round;

    // TODO: zu Testzwecken 3 (aendern)
    public static int numberOfCasualties=3;
    public static int currentPhase;

    public static final int GAME_START = 0;
    public static final int PHASE_WEREWOLF = 1;
    public static final int PHASE_WITCH = 2;
    public static final int PHASE_SEER = 3;
    public static final int PHASE_DAY = 4;


    public long getCurrentTime(){
        return System.currentTimeMillis();
    }
    public int getCurrentPhase() { return currentPhase; }
    public void setCurrentPhase(int currentPhase) {
        this.currentPhase = currentPhase;
    }

    public int getNumberOfCasualties() { return numberOfCasualties; }
    public void setNumberOfCasualties(int numberOfCasualties) {
        this.numberOfCasualties = numberOfCasualties;
    }

    public ArrayList<Player> getPlayersList() {
        return players;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void setPlayers(ArrayList<Player> playerList) {
        this.players = playerList;
    }
}
