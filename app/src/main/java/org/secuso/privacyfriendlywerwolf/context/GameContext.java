package org.secuso.privacyfriendlywerwolf.context;

import android.util.Log;

import org.secuso.privacyfriendlywerwolf.model.Player;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * singleton, which holds the players of the game
 *
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class GameContext  {

    //TODO: information about which player died
    private static final String TAG = "PlayerHolder";
    private static final GameContext GAME_CONTEXT = new GameContext();

    //TODO: enum for the phases
    public static final int GAME_START = 0;
    public static final int PHASE_WEREWOLF = 1;
    public static final int PHASE_WITCH = 2;
    public static final int PHASE_SEER = 3;
    public static final int PHASE_DAY = 4;

    private String classID = "GameContext";
    private List<Player> players = new ArrayList<Player>();
    private int currentRound;
    private int currentPhase;
    private Timestamp roundTime;


    public GameContext() {
        Log.d(TAG, "PlayerHolder singleton created");
    }

    public static GameContext getInstance() {

        return GAME_CONTEXT;
    }

    public int getCurrentPhase() {

        return currentPhase;
    }
    public void setCurrentPhase(int currentPhase) {

        this.currentPhase = currentPhase;
    }

    public List<Player> getPlayersList() {

        return players;
    }

    public void addPlayer(Player player) {

        players.add(player);
    }

    public void setPlayers(List<Player> playerList) {

        this.players = playerList;
    }

    public Player getPlayerByName(String playerName){
        for(Player player : players){
            if(playerName.equals(player.getPlayerName())){
                return player;
            }
        }
        //TODO: throw playerNotFoundException
        return null;
    }

    public void copy(GameContext gc) {

        //TODO: implement for all attributes
        this.setPlayers(gc.getPlayersList());

    }
}
