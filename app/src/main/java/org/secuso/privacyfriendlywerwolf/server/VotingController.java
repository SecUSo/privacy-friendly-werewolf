package org.secuso.privacyfriendlywerwolf.server;

import android.util.Log;

import org.secuso.privacyfriendlywerwolf.model.Player;

import java.util.LinkedHashMap;

/**
 * singleton, which handles the voting
 *
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class VotingController {

    private static final VotingController VOTING_CONTROLLER = new VotingController();
    private static final String TAG = "VotingController";

    /**
     * the amount of votes needed to finish the voting
     */
    private int countAllVotings;

    /**
     * the amount of current processed votes
     */
    private int countCurrentVotings;


    /**
     * saves the player and how much votes the player got.
     * its necessary to have a LinkedHashMap, because the order
     * of insertion is relevant.
     */
    private LinkedHashMap<Player, Integer> playersToVotesMap;

    private VotingController() {
        Log.d(TAG, "VotingController singleton created");
    }

    /**
     * starts the voting.
     * <p>
     * The amount of a relevant clients decided how long the voting will go on.
     *
     * @param countRelevantClients how many votes needed to finish voting
     */
    public void startVoting(int countRelevantClients) {
        countCurrentVotings = 0;
        countAllVotings = countRelevantClients;
        playersToVotesMap = new LinkedHashMap<>();
    }

    /**
     * checks, if all votes are received
     *
     * @return if all votes are received
     */
    public boolean allVotesReceived() {
        Log.d(TAG, "Check if allVotesReceived: currentVotes: " + countCurrentVotings + " AllNeededVotes: " + countAllVotings);
        if (countAllVotings == countCurrentVotings) {
            return true;
        }
        return false;
    }

    /**
     * Returns the voting winner. The voting winner is the player with the most votes.
     * In a stalemate situation, the player who was put first wins.
     *
     * @return the winning player
     */
    public Player getVotingWinner() {
        Log.d(TAG, "getVoting Winner");
        Integer highestVote = 0;
        Player voteWinner = null;
        for (Player player : playersToVotesMap.keySet()) {
            Integer votes = playersToVotesMap.get(player);
            if (votes > highestVote) {
                voteWinner = player;
                highestVote = votes;
            }
        }
        if (voteWinner != null) {
            Log.d(TAG, "Voting Winner is: " + voteWinner.getPlayerName());
        }
        return voteWinner;
    }

    /**
     * adds a vote to the player
     *
     * @param player, the player who gets the vote
     */
    public void addVote(Player player) {
        Log.d(TAG, "currentVotes: " + countCurrentVotings);
        Log.d(TAG, "AllNeededVotes: " + countAllVotings);
        countCurrentVotings++;
        if (player != null) {
            Integer count = playersToVotesMap.get(player);
            if (count == null) {
                playersToVotesMap.put(player, 1);
            } else {
                playersToVotesMap.put(player, count + 1);
            }
        }
        Log.d(TAG, "currentVotes: " + countCurrentVotings);
        Log.d(TAG, "AllNeededVotes: " + countAllVotings);
    }

    public static VotingController getInstance() {
        return VOTING_CONTROLLER;

    }

    /**
     * setter just for unitTesting purpose
     *
     * @param playersToVotesMap
     */
    public void setPlayersToVotesMap(LinkedHashMap<Player, Integer> playersToVotesMap) {
        this.playersToVotesMap = playersToVotesMap;
    }
}
