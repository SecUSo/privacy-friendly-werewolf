package org.secuso.privacyfriendlywerwolf.util;

import android.util.Log;

import org.secuso.privacyfriendlywerwolf.context.GameContext;
import org.secuso.privacyfriendlywerwolf.model.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tobi on 27.11.2016.
 */

public class GameUtil {

    private static final String TAG = "GameUtil";

    public static List<Player> getAllLivingPlayers() {
        List<Player> citizen = new ArrayList<>();
        List<Player> players = GameContext.getInstance().getPlayersList();
        for (Player player : players) {
            if (!player.isDead()) {
                citizen.add(player);
            }
        }
        return citizen;
    }

    /**
     * Count the number of living innocent players
     * @return number of living non-werewolves
     */
    public static int getInnocentCount() {
        List<Player> players = GameContext.getInstance().getPlayersList();
        int count = 0;
        for (Player p : players) {
            if(!p.isDead() && p.getPlayerRole()!= Player.Role.WEREWOLF) {
                count++;
            }
        }
        return count;
    }

    /**
     * Count the number of living werewolve players
     * @return number of living werewolves
     */
    public static int getWerewolfCount() {
        List<Player> players = GameContext.getInstance().getPlayersList();
        int count = 0;
        for (Player p : players) {
            if(!p.isDead() && p.getPlayerRole()== Player.Role.WEREWOLF) {
                count++;
            }
        }
        return count;
    }



    public static boolean isSeerAlive() {
        List<Player> players = GameContext.getInstance().getPlayersList();
        boolean alive = false;
        for (Player player : players) {
            if (player.getPlayerRole().equals(Player.Role.SEER)) {
                if (!player.isDead()) {
                    alive = true;
                }
            }
        }
        return alive;
    }

    public static boolean isWitchAlive() {
        List<Player> players = GameContext.getInstance().getPlayersList();
        boolean alive = false;
        for (Player player : players) {
            if (player.getPlayerRole().equals(Player.Role.WITCH)) {
                if (!player.isDead()) {
                    alive = true;
                }
            }
        }
        return alive;
    }

    public static List<Player> getAllLivingWerewolfes() {
        List<Player> werewolfes = new ArrayList<>();
        List<Player> players = GameContext.getInstance().getPlayersList();
        for (Player player : players) {
            Log.d(TAG, "player " + player + " is living and werewolf?");
            if (player.getPlayerRole().equals(Player.Role.WEREWOLF) && !player.isDead()) {
                Log.d(TAG, "yes it is");
                werewolfes.add(player);
            }
        }
        return werewolfes;
    }
}
