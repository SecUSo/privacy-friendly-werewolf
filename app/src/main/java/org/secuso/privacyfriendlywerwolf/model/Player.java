package org.secuso.privacyfriendlywerwolf.model;

import java.io.Serializable;

/**
 * Created by Tobi on 27.11.2016.
 */

public class Player implements Serializable {

    private String name;
    private PlayerRole playerRole;
    private boolean isDead = false;

    public Player() {

        isDead = false;
    }

    public Player(String playerName) {

        this();
        this.name = playerName;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }



    public PlayerRole getPlayerRole() {
        return playerRole;
    }

    public String getName() {
        return name;
    }


    public void setPlayerRoles(PlayerRole playerRole) {
        this.playerRole = playerRole;
    }

    public void setName(String name) {
        this.name = name;
    }




}
