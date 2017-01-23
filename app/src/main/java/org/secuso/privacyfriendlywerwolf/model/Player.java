package org.secuso.privacyfriendlywerwolf.model;

import java.io.Serializable;

/**
 * Created by Tobi on 27.11.2016.
 */

public class Player implements Serializable {

    private String name;
    // private PlayerRole playerRole;
    private String playerRole;
    private boolean isDead = false;
    private static long serialVersionUID = 1L;

    public Player() {

        // TODO: change
        playerRole = "CITIZEN";
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

    public String getPlayerRole() {

        return playerRole;
    }

    public String getPlayerName() {
        return name;
    }


    public void setPlayerRole(String playerRole) {

        this.playerRole = playerRole;
    }

    public void setName(String name) {
        this.name = name;
    }


    public long getPlayerId() {
        return serialVersionUID;
    }
}
