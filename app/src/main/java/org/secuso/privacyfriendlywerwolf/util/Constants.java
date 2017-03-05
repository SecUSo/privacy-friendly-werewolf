package org.secuso.privacyfriendlywerwolf.util;

/**
 * Constants class for easy access.
 *
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class Constants {

    /**
     * activity constants
     */
    public static final String PLAYERNAME_PUTEXTRA = "playerName";


    /**
     * constants for settings keys
     */
    public static final String pref_timer_night = "pref_timer_night";
    public static final String pref_timer_day = "pref_timer_day";
    public static final String pref_timer_seer = "pref_timer_seer";
    public static final String pref_timer_witch = "pref_timer_witch";
    public static final String pref_playerName = "pref_playerName";
    public static final String pref_werewolf_player = "pref_werewolf_player";
    public static final String pref_seer_player = "pref_seer_player";
    public static final String pref_witch_player = "pref_witch_player";
    public static final String pref_timer_prefix = "pref_timer";
    public static final String pref_sound_background = "pref_sound_background";

    /**
     * gameflow constants
     */
    public static final String EMPTY_VOTING_PLAYER = "";
    public static final long NO_PLAYER_KILLED_THIS_ROUND = -1;
    public static final long SERVER_PLAYER_ID = 0;

    /**
     * Set this to false, if you want to deactivate features that hinder or slow down
     * your testing. This will deactivate following features: (1) End game trigger.
     * (2) Next Button not active in night phases.
     */
    public static final boolean GAME_FEATURES_ACTIVATED = true;


}
