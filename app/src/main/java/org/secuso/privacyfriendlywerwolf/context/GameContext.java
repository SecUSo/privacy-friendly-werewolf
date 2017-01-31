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

    public enum Phase { GAME_START(0),PHASE_WEREWOLF_START(1),PHASE_WEREWOLF_VOTING(2),
        PHASE_WEREWOLF_END(3),PHASE_WITCH(4),PHASE_SEER(5),PHASE_DAY_START(6),PHASE_DAY_VOTING(7),
        PHASE_DAY_END(8);

        private int id;
        Phase(int id) {
            this.id = id;
        }
        public int getId() {
            return this.id;
        }
    }

    // TODO: remove them
    public static final int GAME_START = 0;
    public static final int PHASE_WEREWOLF_START = 1;
    public static final int PHASE_WEREWOLF_VOTING = 2;
    public static final int PHASE_WEREWOLF_END = 3;
    public static final int PHASE_WITCH = 4;
    public static final int PHASE_SEER = 5;
    public static final int PHASE_DAY_START = 6;
    public static final int PHASE_DAY_VOTING = 7;
    public static final int PHASE_DAY_END = 8;

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
