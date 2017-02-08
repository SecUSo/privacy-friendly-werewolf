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

    public static void foo(){
        // do smth
    }

    public static List<Player> getAllLivingPlayers (){
        List<Player> citizen = new ArrayList<>();
        List<Player> players = GameContext.getInstance().getPlayersList();
        for(Player player : players){
            if(!player.isDead()) {
                citizen.add(player);
            }
        }
        return citizen;
    }

    public static List<Player> getAllLivingWerewolfes(){
        List<Player> werewolfes = new ArrayList<>();
        List<Player> players = GameContext.getInstance().getPlayersList();
        for(Player player : players){
            // if(player.getPlayerRole() instanceof Werewolf && !player.isDead()){
            //     werewolfes.add(player);
            //}
            Log.d(TAG, "player " + player + " is living and werewolf?");
            if(player.getPlayerRole().equals(Player.Role.WEREWOLF) && !player.isDead()) {
                Log.d(TAG, "yes it is");
                werewolfes.add(player);
            }
        }
        return werewolfes;
    }
}
