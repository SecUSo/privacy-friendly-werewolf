package org.secuso.privacyfriendlywerwolf.controller;

import android.util.Log;

import org.secuso.privacyfriendlywerwolf.model.Player;

import java.util.HashMap;

/**
 * singleton, which handles the voting
 *
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class VotingController {

    private static final VotingController VOTING_CONTROLLER = new VotingController();
    private static final String TAG = "VotingController";

    private int countAllVotings;
    private int countCurrentVotings;

    private HashMap<Player, Integer> playersToVotesMap;

    private VotingController() {
        Log.d(TAG, "VotingController singleton created");
        playersToVotesMap = new HashMap<>();
    }

    public void startVoting(int countClients){
        countCurrentVotings = 0;
        countAllVotings = countClients;
        playersToVotesMap = new HashMap<>();
    }

    public boolean allVotesReceived() {
        Log.d(TAG, "Check if allVotesReceived: currentVotes: "+ countCurrentVotings+" AllNeededVotes: "+ countAllVotings);
        if (countAllVotings == countCurrentVotings) {
            return true;
        }
        return false;
    }

    public Player getVotingWinner(){
        //TODO: implement randomizing if same result
        Log.d(TAG, "getVoting Winner");
        Integer highestVote = 0;
        Player voteWinner = null;
        for(Player player : playersToVotesMap.keySet()){
            Integer votes = playersToVotesMap.get(player);
            if(votes > highestVote) {
                voteWinner = player;
            }
        }
        Log.d(TAG, "Voting Winner is: "+ voteWinner.getPlayerName());
        return voteWinner;
    }

    public void addVote(Player player) {
        Log.d(TAG, "currentVotes: "+ countCurrentVotings);
        Log.d(TAG, "AllNeededVotes: "+ countAllVotings);
        countCurrentVotings++;
        Integer count = playersToVotesMap.get(player);
        if (count == null) {
            playersToVotesMap.put(player, 1);
        } else {
            playersToVotesMap.put(player, count + 1);
        }
        Log.d(TAG, "currentVotes: "+ countCurrentVotings);
        Log.d(TAG, "AllNeededVotes: "+ countAllVotings);
    }

    public static VotingController getInstance() {
        return VOTING_CONTROLLER;

    }

    public int getCountAllVotings() {
        return countAllVotings;
    }

    public void setCountAllVotings(int countAllVotings) {
        this.countAllVotings = countAllVotings;
    }

    public int getCountCurrentVotings() {
        return countCurrentVotings;
    }

    public void setCountCurrentVotings(int countCurrentVotings) {
        this.countCurrentVotings = countCurrentVotings;
    }
}
