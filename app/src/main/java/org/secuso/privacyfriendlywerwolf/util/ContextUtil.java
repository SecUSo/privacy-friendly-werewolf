package org.secuso.privacyfriendlywerwolf.util;


import org.secuso.privacyfriendlywerwolf.context.GameContext;
import org.secuso.privacyfriendlywerwolf.model.Player;

/**
 * Utility class regarding the the game Context
 *
 * @author Tobias Kowalski <Tobias.Kowalski@stud.tu-darmstadt.de>
 */
public class ContextUtil {

    public static long lastKilledPlayerID = Constants.NO_PLAYER_KILLED_THIS_ROUND;
    public static long lastKilledPlayerIDByWitch = Constants.NO_PLAYER_KILLED_THIS_ROUND;

    public static long duplicate_player_indicator = 1;

    public static boolean IS_FIRST_ROUND = true;
    public static boolean END_OF_ROUND = false;

    public static int RANDOM_INDEX = -1;

    public static boolean isDuplicateName(String playerName){
       boolean result = false;
        GameContext context = GameContext.getInstance();
        for (Player player : context.getPlayersList()){
            if(player.getPlayerName().equals(playerName)) result = true;
        }
        return result;
    }

    public static void destroy() {
        lastKilledPlayerID = Constants.NO_PLAYER_KILLED_THIS_ROUND;
        lastKilledPlayerIDByWitch = Constants.NO_PLAYER_KILLED_THIS_ROUND;

        duplicate_player_indicator = 1;

        IS_FIRST_ROUND = true;

        RANDOM_INDEX = -1;
    }
}
