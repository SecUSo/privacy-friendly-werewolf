package org.secuso.privacyfriendlywerwolf.util;

import org.secuso.privacyfriendlywerwolf.context.GameContext;
import org.secuso.privacyfriendlywerwolf.model.Player;

/**
 * Utility class regarding the the game Context
 *
 * @author Tobias Kowalski <Tobias.Kowalski@stud.tu-darmstadt.de>
 */
public class ContextUtil {

    public static long lastKilledPlayerID = -1;
    public static long lastKilledPlayerIDByWitch = -1;

    public static long duplicate_player_indicator = 1;

    public static boolean IS_FIRST_ROUND = true;

    public static boolean isDuplicateName(String playerName){
       boolean result = false;
        GameContext context = GameContext.getInstance();
        for (Player player : context.getPlayersList()){
            if(player.getPlayerName().equals(playerName)) result = true;
        }
        return result;
    }
}
