package org.secuso.privacyfriendlywerwolf.util;

import org.secuso.privacyfriendlywerwolf.context.GameContext;
import org.secuso.privacyfriendlywerwolf.model.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tobi on 27.11.2016.
 */

public class GameUtil {

    public static void foo(){
        // do smth
    }

    public static List<Player> getAllLivingCitizen (){
        List<Player> citizen = new ArrayList<>();
        List<Player> players = GameContext.getInstance().getPlayersList();
        for(Player player : players){
            // if(player.getPlayerRole() instanceof Citizen && !player.isDead()){
            //    citizen.add(player);
            // }
            if(player.getPlayerRole().equals(Player.Role.CITIZEN)) {
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
            if(player.getPlayerRole().equals(Player.Role.WEREWOLF)) {
                werewolfes.add(player);
            }
        }
        return werewolfes;
    }
}
