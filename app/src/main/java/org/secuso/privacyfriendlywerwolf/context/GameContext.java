package org.secuso.privacyfriendlywerwolf.context;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.secuso.privacyfriendlywerwolf.activity.MainActivity;
import org.secuso.privacyfriendlywerwolf.enums.GamePhaseEnum;
import org.secuso.privacyfriendlywerwolf.enums.SettingsEnum;
import org.secuso.privacyfriendlywerwolf.model.Player;
import org.secuso.privacyfriendlywerwolf.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * singleton, which holds important game information including:
 * <p>
 * - player's
 * - phases
 * - settings
 * <p>
 * GameContext is the main transfer object, to synchronize the gameInformation with the
 * clients / smartphones
 *
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class GameContext {

    private static final String TAG = "GameContext";
    private static final GameContext GAME_CONTEXT = new GameContext();


    private List<Player> players = new ArrayList<>();
    private Map<SettingsEnum, String> settings = new HashMap<>();
    private GamePhaseEnum currentPhase;


    /**
     * Constructor to create a new GameContext singleton
     * Fill the settings with default settings from the app
     */
    private GameContext() {
        Log.d(TAG, "GameContext singleton created");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.getContextOfApplication());
        // set some default configurations
        settings.put(SettingsEnum.TIME_WEREWOLF, String.valueOf(sharedPref.getInt(Constants.pref_timer_night, 60)));
        settings.put(SettingsEnum.TIME_WITCH, String.valueOf(sharedPref.getInt(Constants.pref_timer_witch, 60)));
        settings.put(SettingsEnum.TIME_SEER, String.valueOf(sharedPref.getInt(Constants.pref_timer_seer, 60)));
        settings.put(SettingsEnum.TIME_VILLAGER, String.valueOf(sharedPref.getInt(Constants.pref_timer_seer, 300)));
    }

    public void updateSetting(SettingsEnum setting, String pref) {
        Log.d(TAG, "Updated preference for: " + setting.toString() + " to " + pref);
        settings.put(setting, pref);
    }

    /**
     * Copy an existing GameContext into this GameContext instance
     *
     * @param gc the existing GameContext instance
     */
    public void copy(GameContext gc) {

        this.setPlayers(gc.getPlayersList());
        this.setSettings(gc.getSettings());
        this.setCurrentPhase(gc.getCurrentPhase());

    }

    public Player getPlayerByName(String playerName) {
        for (Player player : players) {
            if (playerName.equals(player.getPlayerName())) {
                return player;
            }
        }
        //TODO: throw playerNotFoundException
        return null;
    }

    public Player getPlayerById(Long id) {
        for (Player player : players) {
            if (id.equals(player.getPlayerId())) {
                return player;
            }
        }
        //TODO: throw playerNotFoundException
        Log.d(TAG, "getPlayerById: Player not Found!, id is " + id);
        return null;
    }

    /**
     * Clears the GameContext object
     */
    public void destroy() {
        players = new ArrayList<>();
        currentPhase = null;
        settings.put(SettingsEnum.WITCH_ELIXIR, null);
        settings.put(SettingsEnum.WITCH_POISON, null);
    }

    public static GameContext getInstance() {

        return GAME_CONTEXT;
    }

    public GamePhaseEnum getCurrentPhase() {

        return currentPhase;
    }

    public void setCurrentPhase(GamePhaseEnum currentPhase) {

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

    public void setSetting(SettingsEnum key, String value) {
        this.settings.put(key, value);
    }

    public String getSetting(SettingsEnum key) {
        return this.settings.get(key);
    }


    public Map<SettingsEnum, String> getSettings() {
        return settings;
    }

    public void setSettings(Map<SettingsEnum, String> settings) {
        this.settings = settings;
    }


}
