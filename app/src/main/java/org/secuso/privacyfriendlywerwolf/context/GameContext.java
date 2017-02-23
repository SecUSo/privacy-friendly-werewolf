package org.secuso.privacyfriendlywerwolf.context;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.secuso.privacyfriendlywerwolf.activity.MainActivity;
import org.secuso.privacyfriendlywerwolf.model.Player;
import org.secuso.privacyfriendlywerwolf.util.Constants;

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

    private static final String TAG = "PlayerHolder";
    private static final GameContext GAME_CONTEXT = new GameContext();

    /**
     * All game phases are defined here
     */
    public enum Phase { GAME_START(0),PHASE_WEREWOLF_START(1),PHASE_WEREWOLF_VOTING(2),
        PHASE_WEREWOLF_END(3),PHASE_WITCH(4),PHASE_SEER(5),PHASE_DAY_START(6),PHASE_DAY_VOTING(7),
        PHASE_DAY_END(8), PHASE_WITCH_ELIXIR(9), PHASE_WITCH_POISON(10);

        private int id;
        Phase(int id) {
            this.id = id;
        }
        public int getId() {
            return this.id;
        }
    }

    /**
     * All game settings are defined here
     */
    public enum Setting { TIME_WEREWOLF, TIME_WITCH, TIME_SEER, TIME_VILLAGER, WITCH_POISON, WITCH_ELIXIR, KILLED_BY_WEREWOLF }

    private List<Player> players = new ArrayList<Player>();
    private Map<Setting,String> settings = new HashMap<>();
    private Phase currentPhase;


    /**
     * Constructor to create a new GameContext singleton
     * Fill the settings with default settings from the app
     */
    public GameContext() {
        Log.d(TAG, "PlayerHolder singleton created");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.getContextOfApplication());
        // set some default configurations
        settings.put(Setting.TIME_WEREWOLF, String.valueOf(sharedPref.getInt(Constants.pref_timer_night, 60)));
        settings.put(Setting.TIME_WITCH, String.valueOf(sharedPref.getInt(Constants.pref_timer_witch, 60)));
        settings.put(Setting.TIME_SEER, String.valueOf(sharedPref.getInt(Constants.pref_timer_seer, 60)));
        settings.put(Setting.TIME_VILLAGER, String.valueOf(sharedPref.getInt(Constants.pref_timer_seer, 300)));
    }

    public void updateSetting(Setting setting, String pref){
        settings.put(setting, pref);
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
            Log.d(TAG, "equals: " + id.equals(player.getPlayerId()));
            if(id.equals(player.getPlayerId())){
                return player;
            }
        }
        //TODO: throw playerNotFoundException
        return null;
    }

    public Map<Setting, String> getSettings() {
        return settings;
    }

    public void setSettings(Map<Setting, String> settings) {
        this.settings = settings;
    }

    /**
     * Copy an existing GameContext into this GameContext instance
     * @param gc the existing GameContext instance
     */
    public void copy(GameContext gc) {

        this.setPlayers(gc.getPlayersList());
        this.setSettings(gc.getSettings());
        this.setCurrentPhase(gc.getCurrentPhase());

    }

    /**
     * Clears the GameContext object
     */
    public void destroy() {
        players = new ArrayList<>();
        settings = new HashMap<>();
        currentPhase = null;
    }
}
