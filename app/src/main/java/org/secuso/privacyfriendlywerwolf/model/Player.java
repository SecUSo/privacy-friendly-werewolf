package org.secuso.privacyfriendlywerwolf.model;

import org.secuso.privacyfriendlywerwolf.R;

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
    private boolean isHost = false;
    private static long serialVersionUID = 1L;

    /**
     * Every player can act in a certain role, which are defined here
     */
    public enum Role {
        WEREWOLF(R.string.role_werewolf),
        CITIZEN(R.string.role_citizen),
        WITCH(R.string.role_witch),
        SEER(R.string.role_seer);

        private int roleName;
        Role(int roleName) {
            this.roleName = roleName;
        }
        public int getRole() {
            return this.roleName;
        }
    }

    /**
     * Constructor to create an empty player
     */
    public Player() {
        isDead = false;
    }

    /**
     * Constructor to create a new player with the given name
     * @param playerName the player's name
     */
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

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
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
