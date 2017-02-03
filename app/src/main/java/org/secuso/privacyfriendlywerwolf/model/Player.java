package org.secuso.privacyfriendlywerwolf.model;

import java.io.Serializable;

/**
 * This is a Player object for the game
 * Created by Tobi on 27.11.2016.
 */
public class Player implements Serializable {

    private String name;
    private Role playerRole;
    private long playerId;
    private boolean isDead = false;
    private static long serialVersionUID = 1L;

    public enum Role { WEREWOLF, CITIZEN, WITCH, SEER }

    public Player() {

        // TODO: change
        // playerRole = CITIZEN;
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

    public Role getPlayerRole() {

        return playerRole;
    }

    public String getPlayerName() {
        return name;
    }


    public void setPlayerRole(Role playerRole) {

        this.playerRole = playerRole;
    }

    public void setName(String name) {
        this.name = name;
    }


    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }
}
