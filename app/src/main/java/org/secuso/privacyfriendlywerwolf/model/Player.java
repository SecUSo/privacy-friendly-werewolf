package org.secuso.privacyfriendlywerwolf.model;

import org.secuso.privacyfriendlywerwolf.R;

import java.io.Serializable;


/**
 * player model of the game
 *
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class Player implements Serializable {
    /**
     * the name of the player
     */
    private String name;
    /**
     * the role of the player
     */
    private Role playerRole;

    /**
     * the unique key of the player
     */
    private long playerId;
    /**
     * the player's death status
     */
    private boolean isDead = false;
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
     *
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
