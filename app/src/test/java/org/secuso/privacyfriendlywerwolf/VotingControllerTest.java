package org.secuso.privacyfriendlywerwolf;

import android.util.Log;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.secuso.privacyfriendlywerwolf.model.Player;
import org.secuso.privacyfriendlywerwolf.server.VotingController;

import java.util.HashMap;
import java.util.Map;

/**
 * Unit Tests for VotingController
 *
 * @author Tobias.Kowalski <Tobias.Kowalski@stud.tu-darmstadt.de>
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class VotingControllerTest {

    VotingController votingController;
    Player player1;
    Player player2;
    Player player3;
    private Map<Player, Integer> playerToVotesTestMap;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(Log.class);
        votingController = VotingController.getInstance();
        player1 = new Player();
        player2 = new Player();
        player3 = new Player();
        player1.setName("Test1");
        player2.setName("Test2");
        player3.setName("Test3");
        playerToVotesTestMap = new HashMap<>();

    }

    @Test
    public void shouldFindWinnerTest1() {
        playerToVotesTestMap.put(player1, 2);
        votingController.setPlayersToVotesMap(playerToVotesTestMap);
        Player winner = votingController.getVotingWinner();
        Assert.assertEquals("Test1", winner.getPlayerName());
    }

    @Test
    public void shouldFindWinnerTest1WithMore() {
        playerToVotesTestMap.put(player1, 2);
        playerToVotesTestMap.put(player2, 1);
        votingController.setPlayersToVotesMap(playerToVotesTestMap);
        Player winner = votingController.getVotingWinner();
        Assert.assertEquals("Test1", winner.getPlayerName());
    }

    @Test
    public void shouldFindWinnerTest2() {
        playerToVotesTestMap.put(player1, 1);
        playerToVotesTestMap.put(player2, 2);
        votingController.setPlayersToVotesMap(playerToVotesTestMap);
        Player winner = votingController.getVotingWinner();
        Assert.assertEquals("Test2", winner.getPlayerName());
    }

    @Test
    public void shouldFindWinnerTest2WithMore() {
        playerToVotesTestMap.put(player1, 1);
        playerToVotesTestMap.put(player2, 2);
        playerToVotesTestMap.put(player3, 1);
        votingController.setPlayersToVotesMap(playerToVotesTestMap);
        Player winner = votingController.getVotingWinner();
        Assert.assertEquals("Test2", winner.getPlayerName());
    }

    @Test
    public void shouldFindWinnerTest3() {
        playerToVotesTestMap.put(player1, 2);
        playerToVotesTestMap.put(player2, 1);
        playerToVotesTestMap.put(player3, 3);
        votingController.setPlayersToVotesMap(playerToVotesTestMap);
        Player winner = votingController.getVotingWinner();
        Assert.assertEquals("Test3", winner.getPlayerName());
    }

    @Test
    public void shouldFindWinnerTest3WithMore() {
        playerToVotesTestMap.put(player1, 6);
        playerToVotesTestMap.put(player2, 4);
        playerToVotesTestMap.put(player3, 5);
        votingController.setPlayersToVotesMap(playerToVotesTestMap);
        Player winner = votingController.getVotingWinner();
        Assert.assertEquals("Test1", winner.getPlayerName());
    }
}
