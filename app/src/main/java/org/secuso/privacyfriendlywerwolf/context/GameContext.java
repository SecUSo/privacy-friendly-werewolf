package org.secuso.privacyfriendlywerwolf.context;

import android.util.Log;

import org.secuso.privacyfriendlywerwolf.model.Player;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public enum Setting { TIME_WEREWOLF, TIME_WITCH, TIME_SEER, TIME_VILLAGER }

    private List<Player> players = new ArrayList<Player>();
    private Map<Setting,String> settings = new HashMap<>();
    private int currentRound;
    private Phase currentPhase;
    private Timestamp roundTime;


    public GameContext() {

        Log.d(TAG, "PlayerHolder singleton created");

        // set some default configurations
        settings.put(Setting.TIME_WEREWOLF, "60");
        settings.put(Setting.TIME_WITCH, "60");
        settings.put(Setting.TIME_SEER, "60");
        settings.put(Setting.TIME_VILLAGER, "300");
    }

    public static GameContext getInstance() {

        return GAME_CONTEXT;
    }

    public Phase getCurrentPhase() {

        return currentPhase;
    }
    public void setCurrentPhase(Phase currentPhase) {

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

    public void setSetting(Setting key, String value) {
        this.settings.put(key, value);
    }

    public String getSetting(Setting key) {
        return this.settings.get(key);
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

    public Player getPlayerById(Long id) {
        for(Player player : players){
            if(id.equals(player.getPlayerId())){
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
