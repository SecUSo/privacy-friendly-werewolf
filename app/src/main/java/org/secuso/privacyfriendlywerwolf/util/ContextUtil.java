package org.secuso.privacyfriendlywerwolf.util;

import org.secuso.privacyfriendlywerwolf.context.GameContext;
import org.secuso.privacyfriendlywerwolf.model.Player;

/**
 * Created by Tobi on 08.02.2017.
 */

public class ContextUtil {

    public static long lastKilledPlayerID = -1;
    public static long lastKilledPlayerIDByWitch = -1;

    public static long duplicate_player_indicator = 1;

    public static boolean isDuplicateName(String playerName){
       boolean result = false;
        GameContext context = GameContext.getInstance();
        for (Player player : context.getPlayersList()){
            if(player.getPlayerName().equals(playerName)) result = true;
        }
        return result;
    }
}
