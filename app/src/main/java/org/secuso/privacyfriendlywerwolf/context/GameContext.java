package org.secuso.privacyfriendlywerwolf.context;

import java.sql.Timestamp;
import java.util.List;

import org.secuso.privacyfriendlywerwolf.model.PlayerRole;

/**
 * Created by Tobi on 27.11.2016.
 */
//TODO: think about static
public class GameContext {



    public static List<PlayerRole> activeRoles;

    //TODO: think correct type
    public static Timestamp roundTime;

    //TODO: think correct type
    public static int round;
}
