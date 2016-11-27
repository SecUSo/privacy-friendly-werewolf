package secuso.org.privacyfriendlywerwolf.model;

import java.util.List;

/**
 * Created by Tobi on 27.11.2016.
 */

public class Player {

    private String name;

    private List<PlayerRole> playerRoles;

    public List<PlayerRole> getPlayerRoles() {
        return playerRoles;
    }

    public String getName() {
        return name;
    }


    public void setPlayerRoles(List<PlayerRole> playerRoles) {
        this.playerRoles = playerRoles;
    }

    public void setName(String name) {
        this.name = name;
    }



}
