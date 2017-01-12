package org.secuso.privacyfriendlywerwolf.context;

import android.util.Log;

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


    public long getCurrentTime(){
        return System.currentTimeMillis();
    }
}
