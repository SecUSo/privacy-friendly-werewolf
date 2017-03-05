package org.secuso.privacyfriendlywerwolf.client;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.activity.GameActivity;
import org.secuso.privacyfriendlywerwolf.activity.MainActivity;
import org.secuso.privacyfriendlywerwolf.activity.StartClientActivity;
import org.secuso.privacyfriendlywerwolf.context.GameContext;
import org.secuso.privacyfriendlywerwolf.enums.GamePhaseEnum;
import org.secuso.privacyfriendlywerwolf.enums.SettingsEnum;
import org.secuso.privacyfriendlywerwolf.model.NetworkPackage;
import org.secuso.privacyfriendlywerwolf.model.Player;
import org.secuso.privacyfriendlywerwolf.server.ServerGameController;
import org.secuso.privacyfriendlywerwolf.util.Constants;
import org.secuso.privacyfriendlywerwolf.util.ContextUtil;
import org.secuso.privacyfriendlywerwolf.util.GameUtil;

import java.util.List;


/**
 * updates the model on the client, aswell as the view on the client and initiates communication to the server
 *
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 * @author Florian Staubach <florian.staubach@stud.tu-darmstadt.de>
 */
public class ClientGameController {

    private static final String TAG = "ClientGameController";
    private static final ClientGameController GAME_CONTROLLER = new ClientGameController();

    // only for the host this serverController is not null
    private ServerGameController serverGameController;

    private Player me;

    long myId;

    private StartClientActivity startClientActivity;
    private GameActivity gameActivity;
    private WebsocketClientHandler websocketClientHandler;
    private GameContext gameContext;


    private ClientGameController() {
        Log.d(TAG, "ClientGameController singleton created");
        websocketClientHandler = new WebsocketClientHandler();
        websocketClientHandler.setGameController(this);
        gameContext = GameContext.getInstance();
    }

    public static ClientGameController getInstance() {
        return GAME_CONTROLLER;

    }

    // enter GameActivity
    public void startGame(GameContext gc) {

        gameContext.copy(gc);
        startClientActivity.startGame();
        //wait some time before the game activity has been created
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
        }

        gameActivity.outputMessage(R.string.progressBar_initial);
        gameActivity.longOutputMessage(R.string.gameStart_hintRoles);


    }


    /**
     * Everybody goes to sleep and the werewolves awake.
     */
    public void initiateWerewolfPhase() {

        ContextUtil.END_OF_ROUND = false;
        Player ownPlayer = GameContext.getInstance().getPlayerById(myId);

        // Host
        if (myId == Constants.SERVER_PLAYER_ID && gameActivity.getMediaPlayer() != null) {

            gameActivity.setMediaPlayer(MediaPlayer.create(gameActivity.getApplicationContext(), R.raw.night_falls));
            gameActivity.getMediaPlayer().start();
            // start the background music
            if(getBackgroundMusicSetting()){
                gameActivity.setBackgroundPlayer(MediaPlayer.create(gameActivity.getApplicationContext(), R.raw.music_background));
                gameActivity.getBackgroundPlayer().setLooping(true);
                gameActivity.getBackgroundPlayer().setVolume(0.2f, 0.2f);
                gameActivity.getBackgroundPlayer().start();
            }

            // wait for night_falls.mp3 to end
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
            }

            gameActivity.outputMessage(R.string.message_villagers_sleep);
            if (!ownPlayer.isDead()) {
                gameActivity.longOutputMessage(R.string.toast_close_eyes);
            }
            gameActivity.setMediaPlayer(MediaPlayer.create(gameActivity.getApplicationContext(), R.raw.close_your_eyes));
            gameActivity.getMediaPlayer().start();

            // wait for close_your_eyes.mp3 to end (2 seconds)
            // give villagers time to close eyes (5 seconds)
            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
            }

        } else { // Clients
            try {
                // wait for night_falls.mp3 to end (3 seconds)
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
            }
            gameActivity.outputMessage(R.string.message_villagers_sleep);
            if (!ownPlayer.isDead()) {
                gameActivity.longOutputMessage(R.string.toast_close_eyes);
            }
            // wait for close_your_eyes.mp3 to end (2 seconds)
            // give villagers time to close eyes (5 seconds)
            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
            }
        }

        gameActivity.outputMessage(R.string.message_werewolfes_awaken);

        gameContext.setSetting(SettingsEnum.KILLED_BY_WEREWOLF, null);

        // Host
        if (myId == Constants.SERVER_PLAYER_ID && gameActivity.getMediaPlayer() != null) {
            gameActivity.setMediaPlayer(MediaPlayer.create(gameActivity.getApplicationContext(), R.raw.wolves_wake));
            gameActivity.getMediaPlayer().start();

            // 3 seconds wolves_wake.mp3
            // 1.5 seconds delay to next mp3
            try {
                Thread.sleep(4500);
            } catch (InterruptedException e) {
                Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
            }

            // in the first round werewolves get some extra
            // time to get to know each other
            if (ContextUtil.IS_FIRST_ROUND) {
                gameActivity.setMediaPlayer(MediaPlayer.create(gameActivity.getApplicationContext(), R.raw.wolves_meet));
                gameActivity.getMediaPlayer().start();
                // 3 seconds wolves_meet.mp3
                // 3 seconds wolves get to know each other
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
                }
            }

        } else { // Clients
            // calculate some extra time in the first round
            // for the werewolves to get to know each other
            if (ContextUtil.IS_FIRST_ROUND) {
                // 3 seconds wolves_wake.mp3
                // 1.5 seconds delay to next mp3
                // 3 seconds wolves_meet.mp3
                // 3 seconds wolves get to know each other
                try {
                    Thread.sleep(10500);
                } catch (InterruptedException e) {
                    Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
                }
            } else {
                // 3 seconds wolves_wake.mp3
                // 1.5 seconds delay to next mp3
                try {
                    Thread.sleep(4500);
                } catch (InterruptedException e) {
                    Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
                }
            }
        }


        if (gameContext.getPlayerById(myId).getPlayerRole() == Player.Role.WEREWOLF && !ownPlayer.isDead()) {
            gameActivity.longOutputMessage(R.string.toast_prepare_vote);

        }

        sendDoneToServer();

    }

    /**
     * The werewolves do a voting for their prey
     */
    public void initiateWerewolfVotingPhase() {

        // soft timer
        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int time = Integer.parseInt(gameContext.getSetting(SettingsEnum.TIME_WEREWOLF));
                gameActivity.makeTimer(time).start();
            }
        });

        // Host
        if (myId == Constants.SERVER_PLAYER_ID && gameActivity.getMediaPlayer() != null) {
            gameActivity.setMediaPlayer(MediaPlayer.create(gameActivity.getApplicationContext(), R.raw.wolves_vote));
            gameActivity.getMediaPlayer().start();

        }

        Player ownPlayer = GameContext.getInstance().getPlayerById(myId);
        if (!ownPlayer.isDead() && ownPlayer.getPlayerRole().equals(Player.Role.WEREWOLF)) {
            // 2.5 seconds delay before voting
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
            }
            gameActivity.openVoting();
        }
    }

    /**
     * The werewolves finished voting and go back to sleep
     */
    public void endWerewolfPhase() {

        gameActivity.outputMessage(R.string.message_werewolfes_sleep);

        Player ownPlayer = GameContext.getInstance().getPlayerById(myId);

        // Host
        if (myId == Constants.SERVER_PLAYER_ID && gameActivity.getMediaPlayer() != null) {
            gameActivity.setMediaPlayer(MediaPlayer.create(gameActivity.getApplicationContext(), R.raw.wolves_sleep));
            gameActivity.getMediaPlayer().start();
        }
        Player roundVictim = getPlayerKilledByWerewolfesName();

        // Werewolf and (living or voted for himself)
        if (gameContext.getPlayerById(myId).getPlayerRole() == Player.Role.WEREWOLF
                && (!ownPlayer.isDead()
                || (roundVictim != null && roundVictim.getPlayerId() == myId))) {
            gameActivity.longOutputMessage(R.string.toast_close_eyes);

        }

        // PlayBack 3-4 seconds
        // wolves close eyes 2-3 seconds
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
        }


        sendDoneToServer();
    }

    /**
     * The witch awakes, and gets to use her healing potion.
     */
    public void initiateWitchElixirPhase() {
        Player roundVictim = getPlayerKilledByWerewolfesName();
        if (GameUtil.isWitchAlive() || (roundVictim != null && roundVictim.getPlayerRole() == Player.Role.WITCH)) {
            gameActivity.outputMessage(R.string.message_witch_awaken);
            gameActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int time = Integer.parseInt(gameContext.getSetting(SettingsEnum.TIME_WITCH));
                    gameActivity.makeTimer(time).start();
                }
            });

            // Host
            if (myId == Constants.SERVER_PLAYER_ID && gameActivity.getMediaPlayer() != null) {
                gameActivity.setMediaPlayer(MediaPlayer.create(gameActivity.getApplicationContext(), R.raw.witch_wakes));
                gameActivity.getMediaPlayer().start();
            }

            // Tell the witch who got killed by Werewolves
            if (gameContext.getPlayerById(myId).getPlayerRole().equals(Player.Role.WITCH)) {
                StringBuilder sb = new StringBuilder();
                sb.append(gameActivity.getString(R.string.gamefield_witch_elixir_action_message1));
                Player victim = getPlayerKilledByWerewolfesName();
                if (victim != null) {
                    sb.append(" ");
                    sb.append(victim.getPlayerName());
                    sb.append(System.getProperty("line.separator"));
                } else {
                    sb.append(R.string.common_no_one);
                }
                gameActivity.showWitchTextPopup(R.string.popup_title_killed_by_wolves, sb.toString());
            }

            // transition from witch_wakes to witch_heal
            try {
                Thread.sleep(5500);
            } catch (InterruptedException e) {
                Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
            }

            if (myId == Constants.SERVER_PLAYER_ID && gameActivity.getMediaPlayer() != null) {
                gameActivity.setMediaPlayer(MediaPlayer.create(gameActivity.getApplicationContext(), R.raw.witch_heal));
                gameActivity.getMediaPlayer().start();
            }

            // if the witch still has a healing potion
            if (gameContext.getSetting(SettingsEnum.WITCH_ELIXIR) == null) {
                if (gameContext.getPlayerById(myId).getPlayerRole().equals(Player.Role.WITCH)) {
                    // witch can use healing potion
                    useElixir();
                }
            } else {
                try {
                    Thread.sleep(4500);
                } catch (InterruptedException e) {
                    Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
                }
                sendDoneToServer();
            }
        } else

        {   // there is currently no witch in the game
            if (myId == Constants.SERVER_PLAYER_ID && gameActivity.getMediaPlayer() != null) {
                //gameActivity.getMediaPlayer().stop();
                gameActivity.setMediaPlayer(MediaPlayer.create(gameActivity.getApplicationContext(), R.raw.witch_down));
                gameActivity.getMediaPlayer().start();
                try {
                    Thread.sleep(3250);
                } catch (InterruptedException e) {
                    Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
                }
            }
            sendDoneToServer();
        }

    }

    // compute the results of the witch's decision
    public void endWitchElixirPhase() {
        Log.d(TAG, "Entering End of WitchElixirPhase!");
        // transition heal popup -> witch popup
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
        }
        String elixirSetting = gameContext.getSetting(SettingsEnum.WITCH_ELIXIR);
        if (myId == Constants.SERVER_PLAYER_ID) {
            ServerGameController.HOST_IS_DONE = true;
            // tell the controller of the Server who was saved (if potion used)
            if (!TextUtils.isEmpty(elixirSetting)) {
                serverGameController.handleWitchResultElixir(Long.parseLong(elixirSetting));
            } else {
                serverGameController.handleWitchResultElixir(null);
            }
        } else {
            // send result to all clients
            try {
                NetworkPackage<GamePhaseEnum> np = new NetworkPackage<>(NetworkPackage.PACKAGE_TYPE.WITCH_RESULT_ELIXIR);
                np.setOption(SettingsEnum.WITCH_ELIXIR.toString(), elixirSetting);
                websocketClientHandler.send(np);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // ask the witch to use her poisoning potion
    public void initiateWitchPoisonPhase() {

        Log.d(TAG, "initiating WitchPoisonPhase()");
        Player roundVictim = getPlayerKilledByWerewolfesName();
        if (GameUtil.isWitchAlive() || (roundVictim != null && roundVictim.getPlayerRole() == Player.Role.WITCH)) {
            if (myId == Constants.SERVER_PLAYER_ID && gameActivity.getMediaPlayer() != null) {
                // prevent music overlays
                if (gameActivity.getMediaPlayer().isPlaying()) {
                    gameActivity.getMediaPlayer().stop();
                }
                gameActivity.setMediaPlayer(MediaPlayer.create(gameActivity.getApplicationContext(), R.raw.witch_poison));
                gameActivity.getMediaPlayer().start();

            }

            // show poison dialog, if conditions are met
            if (gameContext.getSetting(SettingsEnum.WITCH_POISON) == null) {
                if (gameContext.getPlayerById(myId).getPlayerRole().equals(Player.Role.WITCH)) {
                    usePoison();
                }
            } else {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
                }

                if (myId == Constants.SERVER_PLAYER_ID) {
                    gameActivity.setMediaPlayer(MediaPlayer.create(gameActivity.getApplicationContext(), R.raw.witch_sleeps));
                    gameActivity.getMediaPlayer().start();
                }

                gameActivity.outputMessage(R.string.message_witch_sleep);
                if (gameContext.getPlayerById(myId).getPlayerRole().equals(Player.Role.WITCH)) {
                    gameActivity.longOutputMessage(R.string.toast_close_eyes);
                }

                // give witch 5.5 secs to close eyes
                try {
                    Thread.sleep(5500);
                } catch (InterruptedException e) {
                    Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
                }
                sendDoneToServer();
            }

        } else

        {
            sendDoneToServer();
        }

    }

    // computing the result of the poisoning phase
    public void endWitchPoisonPhase() {
        Log.d(TAG, "Entering End of WitchPoisonPhase!");

        String poisonSetting = gameContext.getSetting(SettingsEnum.WITCH_POISON);
        if (myId == Constants.SERVER_PLAYER_ID) {
            ServerGameController.HOST_IS_DONE = true;
            // send result to server controller
            if (!TextUtils.isEmpty(poisonSetting)) {
                serverGameController.handleWitchResultPoison(Long.parseLong(poisonSetting));
            } else {
                serverGameController.handleWitchResultPoison(null);
            }
        } else {
            // send result to all clients
            try {
                NetworkPackage<GamePhaseEnum> np = new NetworkPackage<>(NetworkPackage.PACKAGE_TYPE.WITCH_RESULT_POISON);
                np.setOption(SettingsEnum.WITCH_POISON.toString(), poisonSetting);
                websocketClientHandler.send(np);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * The seer wakes and gets to know a secret identity
     */
    public void initiateSeerPhase() {
        Player roundVictim = getPlayerKilledByWerewolfesName();
        Player witchVictim = getPlayerKilledByWitchName();
        if (GameUtil.isSeerAlive()
                || (roundVictim != null && roundVictim.getPlayerRole() == Player.Role.SEER)
                || (witchVictim != null && witchVictim.getPlayerRole() == Player.Role.SEER)) {
            gameActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int time = Integer.parseInt(gameContext.getSetting(SettingsEnum.TIME_SEER));
                    gameActivity.makeTimer(time).start();
                }
            });
            gameActivity.outputMessage(R.string.message_seer_awaken);
            if (myId == Constants.SERVER_PLAYER_ID && gameActivity.getMediaPlayer() != null) {
                gameActivity.setMediaPlayer(MediaPlayer.create(gameActivity.getApplicationContext(), R.raw.seer_wakes));
                gameActivity.getMediaPlayer().start();
            }

            try {
                Thread.sleep(3750);
            } catch (InterruptedException e) {
                Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
            }


            if (gameContext.getPlayerById(myId).getPlayerRole().equals(Player.Role.SEER)) {
                gameActivity.showTextPopup(R.string.popup_title_seer_power, R.string.popup_text_seer_power);
                gameActivity.outputMessage(R.string.progressBar_choose);
            } else {
                sendDoneToServer();
            }


            // delay GameThread if player clicks really fast
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
            }

        } else {
            if (myId == Constants.SERVER_PLAYER_ID && gameActivity.getMediaPlayer() != null) {
                //gameActivity.getMediaPlayer().stop();
                gameActivity.setMediaPlayer(MediaPlayer.create(gameActivity.getApplicationContext(), R.raw.seer_down));
                gameActivity.getMediaPlayer().start();
            }
            try {
                Thread.sleep(3500);
            } catch (InterruptedException e) {
                Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
            }
            sendDoneToServer();
        }
    }

    // seer goes back to sleep
    public void endSeerPhase() {
        Player roundVictim = getPlayerKilledByWerewolfesName();
        Player witchVictim = getPlayerKilledByWitchName();
        if (GameUtil.isSeerAlive()
                || (roundVictim != null && roundVictim.getPlayerRole() == Player.Role.SEER)
                || (witchVictim != null && witchVictim.getPlayerRole() == Player.Role.SEER)) {
            gameActivity.outputMessage(R.string.message_seer_sleep);
            if (myId == Constants.SERVER_PLAYER_ID && gameActivity.getMediaPlayer() != null) {
                //gameActivity.getMediaPlayer().stop();
                gameActivity.setMediaPlayer(MediaPlayer.create(gameActivity.getApplicationContext(), R.raw.seer_sleeps));
                gameActivity.getMediaPlayer().start();
            }
            if (gameContext.getPlayerById(myId).getPlayerRole().equals(Player.Role.SEER)) {
                gameActivity.longOutputMessage(R.string.toast_close_eyes);
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
            }
        }
        sendDoneToServer();
    }

    /**
     * The day starts. Announce the victims. Check if game has a winner. If not start discussion.
     * Wait for nextButton trigger, to start the voting.
     */
    public void initiateDayPhase() {
        final Player killedPlayer = GameContext.getInstance().getPlayerById(ContextUtil.lastKilledPlayerID);
        final Player killedByWitchPlayer = GameContext.getInstance().getPlayerById(ContextUtil.lastKilledPlayerIDByWitch);
        Player ownPlayer = GameContext.getInstance().getPlayerById(myId);

        // reset variables
        ContextUtil.lastKilledPlayerID = Constants.NO_PLAYER_KILLED_THIS_ROUND;
        ContextUtil.lastKilledPlayerIDByWitch = Constants.NO_PLAYER_KILLED_THIS_ROUND;

        gameActivity.outputMessage(R.string.message_villagers_awaken);

        Log.d(TAG, "Before day_wakes: " + Thread.currentThread().getName());
        if (myId == Constants.SERVER_PLAYER_ID && gameActivity.getMediaPlayer() != null) {

            gameActivity.setMediaPlayer(MediaPlayer.create(gameActivity.getApplicationContext(), R.raw.day_wakes));
            gameActivity.getMediaPlayer().start();

            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
            }
            Log.d(TAG, "Before night_claimed: " + Thread.currentThread().getName());
            gameActivity.setMediaPlayer(MediaPlayer.create(gameActivity.getApplicationContext(), R.raw.night_claimed));
            gameActivity.getMediaPlayer().start();

            try {
                Thread.sleep(2600);
            } catch (InterruptedException e) {
                Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
            }

            // voice output first tells players how many players died in the night
            if (killedPlayer == null && killedByWitchPlayer == null) {
                gameActivity.setMediaPlayer(MediaPlayer.create(gameActivity.getApplicationContext(), R.raw.day_none_died));
                gameActivity.getMediaPlayer().start();
            } else if (killedPlayer != null && killedByWitchPlayer == null) {
                gameActivity.setMediaPlayer(MediaPlayer.create(gameActivity.getApplicationContext(), R.raw.day_one_died));
                gameActivity.getMediaPlayer().start();
            } else if (killedPlayer == null && killedByWitchPlayer != null) {
                gameActivity.setMediaPlayer(MediaPlayer.create(gameActivity.getApplicationContext(), R.raw.day_one_died));
                gameActivity.getMediaPlayer().start();
            } else if (killedPlayer != null && killedByWitchPlayer != null) {
                if (!(killedPlayer.getPlayerName().equals(killedByWitchPlayer.getPlayerName()))) {
                    gameActivity.setMediaPlayer(MediaPlayer.create(gameActivity.getApplicationContext(), R.raw.day_two_died));
                    gameActivity.getMediaPlayer().start();
                } else {
                    gameActivity.setMediaPlayer(MediaPlayer.create(gameActivity.getApplicationContext(), R.raw.day_one_died));
                    gameActivity.getMediaPlayer().start();
                }
            } else {
                Log.d(TAG, "initiateDayPhase(): Something went wrong here");
            }

        } else {
            try {
                Thread.sleep(9100);
            } catch (InterruptedException e) {
                Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
            }
        }

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
        }

        // stop the background music till the end of the day
        if(myId == Constants.SERVER_PLAYER_ID) {
            if (getBackgroundMusicSetting()) {
                gameActivity.getBackgroundPlayer().stop();
            }
        }

        // show players on the UI who died in the night
        if (killedPlayer == null && killedByWitchPlayer == null) {
            gameActivity.showTextPopup(R.string.popup_title_victims, R.string.popup_text_none_died);
        } else if (killedPlayer != null && killedByWitchPlayer == null) {
            gameActivity.showTextPopup(R.string.popup_title_victims, killedPlayer.getPlayerName() + " (" + gameActivity.getResources().getString(killedPlayer.getPlayerRole().getRole()) + ")", R.string.popup_text_killed_this_night);
        } else if (killedPlayer == null && killedByWitchPlayer != null) {
            gameActivity.showTextPopup(R.string.popup_title_victims, killedByWitchPlayer.getPlayerName() + " (" + gameActivity.getResources().getString(killedByWitchPlayer.getPlayerRole().getRole()) + ")",  R.string.popup_text_killed_this_night);
        } else if (killedPlayer != null && killedByWitchPlayer != null) {
            if (!(killedPlayer.getPlayerName().equals(killedByWitchPlayer.getPlayerName()))) {
                Log.d(TAG, "Two died: random generated the num " + ContextUtil.RANDOM_INDEX);
                Player firstVictim = null;
                Player secondVictim = null;
                if (ContextUtil.RANDOM_INDEX == 0) {
                    firstVictim = killedPlayer;
                    secondVictim = killedByWitchPlayer;
                } else if (ContextUtil.RANDOM_INDEX == 1) {
                    firstVictim = killedByWitchPlayer;
                    secondVictim = killedPlayer;
                } else {
                    Log.d(TAG, "Something went wrong with random");
                    firstVictim = new Player("PLAYER_NOT_EXIST");
                    firstVictim.setPlayerRole(Player.Role.CITIZEN);
                    secondVictim = new Player("PLAYER_NOT_EXIST");
                    secondVictim.setPlayerRole(Player.Role.CITIZEN);
                }
                ContextUtil.RANDOM_INDEX = -1;
                gameActivity.showTextPopup(R.string.popup_title_victims, firstVictim.getPlayerName() + " (" + gameActivity.getResources().getString(firstVictim.getPlayerRole().getRole()) + ")"
                        + " & " + secondVictim.getPlayerName() + " (" + gameActivity.getResources().getString(secondVictim.getPlayerRole().getRole()) + ")", R.string.popup_text_killed_this_night_2);
            } else {
                // players killed twice, only show once
                Log.d(TAG, "initiateDayPhase(): Somehow the same player got killed twice");
                gameActivity.showTextPopup(R.string.popup_title_victims, killedPlayer.getPlayerName() + " (" + gameActivity.getResources().getString(killedPlayer.getPlayerRole().getRole()) + ")",  R.string.popup_text_killed_this_night);
            }
        }

        gameActivity.updateGamefield();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
        }

        // check if the endGameTrigger is triggered
        if (gameIsOver() == 0) {
            endGameAndWerewolvesWin();
        } else if (gameIsOver() == 1) {
            endGameAndVillagersWin();
        } else {
            if (!ownPlayer.isDead()) {
                gameActivity.longOutputMessage(R.string.toast_start_discussion);
            }
            gameActivity.outputMessage(R.string.message_villagers_discuss);
            gameActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int time = Integer.parseInt(gameContext.getSetting(SettingsEnum.TIME_VILLAGER));
                    gameActivity.makeTimer(time).start();
                }
            });
            ContextUtil.END_OF_ROUND = true;
            if (myId == Constants.SERVER_PLAYER_ID) {
                gameActivity.activateNextButton();
                if (ContextUtil.IS_FIRST_ROUND) {
                    gameActivity.showFabInfo(R.string.fab_info_start_vote);
                }
            }
        }
    }

    /**
     * Werewolves win if the number of werwolves is
     * greater or equal than the number of citizens
     */
    private void endGameAndWerewolvesWin() {
        gameActivity.outputMessage(R.string.progressBar_wolves_win);

        Player ownPlayer = GameContext.getInstance().getPlayerById(myId);
        if (ownPlayer.getPlayerRole() == Player.Role.WEREWOLF) {
            gameActivity.showGameEndTextView(R.string.text_view_you_win);
        } else {
            gameActivity.showGameEndTextView(R.string.text_view_you_lose);
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
        }
        gameActivity.longOutputMessage(R.string.toast_game_ends);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
        }

        // indicator for the players as to when the game will exit
        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int time = 15;
                gameActivity.makeTimer(time).start();
            }
        });

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
        }
        // reset everything that needs to be resetted
        destroy();
        // go back to main menu
        gameActivity.goToMainActivity();
    }

    /**
     * The citizens win the game, when there is no werewolf left
     * in the game.
     */
    private void endGameAndVillagersWin() {
        gameActivity.outputMessage(R.string.progressBar_villagers_win);

        Player ownPlayer = GameContext.getInstance().getPlayerById(myId);
        if (ownPlayer.getPlayerRole() != Player.Role.WEREWOLF) {
            gameActivity.showGameEndTextView(R.string.text_view_you_win);
        } else {
            gameActivity.showGameEndTextView(R.string.text_view_you_lose);
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
        }
        gameActivity.longOutputMessage(R.string.toast_game_ends);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
        }

        // indicator for the players as to when the game will exit
        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int time = 15;
                gameActivity.makeTimer(time).start();
            }
        });

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
        }
        // reset everything, that needs to be resetted
        destroy();
        // go back to main menu
        gameActivity.goToMainActivity();
    }

    /**
     * After the discussion the Voting begins.
     */
    public void initiateDayVotingPhase() {
        Player ownPlayer = GameContext.getInstance().getPlayerById(myId);
        if (!ownPlayer.isDead()) {
            gameActivity.longOutputMessage(R.string.toast_prepare_vote);
        }
        gameActivity.outputMessage(R.string.message_villagers_vote);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
        }

        // show the voting list
        if (!ownPlayer.isDead()) {
            gameActivity.openVoting();
        }
    }

    /**
     * Compute the results of the voting and send them to all clients.
     * Also check if endGame Trigger is triggered. Wait for the nextButton Press
     * by the Host to enter the next round.
     */
    public void endDayPhase() {
        gameActivity.outputMessage(R.string.progressBar_voting_results);
        Player killedPlayer = GameContext.getInstance().getPlayerById(ContextUtil.lastKilledPlayerID);
        if (killedPlayer != null) {
            gameActivity.showTextPopup(R.string.votingResult_day_title, R.string.votingResult_day_text, killedPlayer.getPlayerName());
        } else {
            Log.d(TAG, "Something went wrong while voting in Day Phase");
        }
        // reset variable
        ContextUtil.lastKilledPlayerID = Constants.NO_PLAYER_KILLED_THIS_ROUND;

        gameActivity.updateGamefield();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
        }

        if (gameIsOver() == 0) {
            endGameAndWerewolvesWin();
        } else if (gameIsOver() == 1) {
            endGameAndVillagersWin();
        } else {
            gameActivity.outputMessage(R.string.message_day_over);
            sendDoneToServer();
            if (myId == Constants.SERVER_PLAYER_ID) {
                gameActivity.activateNextButton();
                if (ContextUtil.IS_FIRST_ROUND) {
                    gameActivity.showFabInfo(R.string.fab_info_start_night);
                }
            }
        }
    }

    /**
     * Determines if the game is over
     *
     * @return -1: No winner, 0: Werewolves win, 1: Villagers win
     */
    private int gameIsOver() {
        if (Constants.GAME_FEATURES_ACTIVATED) {
            int innocentCount = GameUtil.getInnocentCount();
            int werewolfCount = GameUtil.getWerewolfCount();
            // werewolves win
            if (werewolfCount >= innocentCount) {
                return 0;
            } // villagers win
            else if (werewolfCount == 0) {
                return 1;
            } // game continues
            else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    public void useElixir() {
        if (gameContext.getSetting(SettingsEnum.WITCH_ELIXIR) == null) {
            gameActivity.askWitchForElixir();
        } else {
            sendDoneToServer();
        }
    }

    public void usePoison() {
        if (gameContext.getSetting(SettingsEnum.WITCH_POISON) == null) {
            gameActivity.askWitchForPoison();
        } else {
            sendDoneToServer();
        }

    }

    // set a flag, indicating the healing potion was used
    public void usedElixir() {
        //String id = GameContext.getInstance().getSetting(SettingsEnum.KILLED_BY_WEREWOLF);
        gameContext.setSetting(SettingsEnum.WITCH_ELIXIR, String.valueOf(ContextUtil.lastKilledPlayerID));
    }


    /**
     * Method gets called if the witch presses a player card button
     * If the witch has the power to use one then the setting is set in the game context
     *
     * @param selectedPlayer the Player the potion is used on
     */
    public void selectedPlayerForWitch(Player selectedPlayer) {
        getGameActivity().showTextPopup(R.string.popup_title_witch_poison, R.string.popup_text_poisoned, selectedPlayer.getPlayerName());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Log.e(TAG, "D/THREAD_Problem: " + e.getMessage());
        }
        gameActivity.longOutputMessage(R.string.toast_close_eyes);

        // could be redundant
        String id = String.valueOf(selectedPlayer.getPlayerId());
        gameContext.setSetting(SettingsEnum.WITCH_POISON, id);

        endWitchPoisonPhase();
    }


    /**
     * Send the player object voted to the Server by a Client
     * @param player the player voted
     */
    public void sendVotingResult(Player player) {

        if (player != null) {
            // host
            if (myId == Constants.SERVER_PLAYER_ID) {
                //ServerGameController.HOST_IS_DONE = true;
                serverGameController.handleVotingResult(player.getPlayerName());
            } else {
                try {
                    NetworkPackage<String> np = new NetworkPackage<>(NetworkPackage.PACKAGE_TYPE.VOTING_RESULT);
                    np.setPayload(player.getPlayerName());
                    websocketClientHandler.send(np);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (myId == Constants.SERVER_PLAYER_ID) {
                //ServerGameController.HOST_IS_DONE = true;
                serverGameController.handleVotingResult(Constants.EMPTY_VOTING_PLAYER);
            } else {
                try {
                    NetworkPackage<String> np = new NetworkPackage<String>(NetworkPackage.PACKAGE_TYPE.VOTING_RESULT);
                    np.setPayload("");
                    websocketClientHandler.send(np);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }


    /**
     * Updates the Client's GameContext after getting information about
     * the voting winner.
     * @param playerName name of the voting winner
     */
    public void handleVotingResult(String playerName) {

        if (!TextUtils.isEmpty(playerName)) {
            Log.d(TAG, "voting_result received. Kill this guy: " + playerName);
            Player playerToKill = GameContext.getInstance().getPlayerByName(playerName);
            Log.d(TAG, "Player " + getMyPlayer() + " successfully deleted " + playerToKill.getPlayerName() + " after Voting");
            playerToKill.setDead(true);
            ContextUtil.lastKilledPlayerID = playerToKill.getPlayerId();
            gameContext.setSetting(SettingsEnum.KILLED_BY_WEREWOLF, String.valueOf(playerToKill.getPlayerId()));
        }

        sendDoneToServer();
    }

    /**
     * Updates the Client's GameContext after getting information about
     * the poisoned player.
     * @param playerName name of the poisoned player
     */
    public void handleWitchPoisonResult(String playerName) {
        gameActivity.outputMessage(R.string.message_witch_sleep);
        if (!TextUtils.isEmpty(playerName)) {
            Player playerToKill = GameContext.getInstance().getPlayerByName(playerName);
            playerToKill.setDead(true);
            ContextUtil.lastKilledPlayerIDByWitch = playerToKill.getPlayerId();
            gameContext.setSetting(SettingsEnum.WITCH_POISON, String.valueOf(playerToKill.getPlayerId()));
        }

        sendDoneToServer();
    }

    /**
     * Updates the Client's GameContext after getting information about
     * the saved player.
     * @param playerName name of the saved player
     */
    public void handleWitchElixirResult(String playerName) {
        if (!TextUtils.isEmpty(playerName)) {
            Player playerToSave = GameContext.getInstance().getPlayerByName(playerName);
            playerToSave.setDead(false);
            ContextUtil.lastKilledPlayerID = Constants.NO_PLAYER_KILLED_THIS_ROUND;
            gameContext.setSetting(SettingsEnum.WITCH_ELIXIR, "used");
        }


        sendDoneToServer();
    }


    public void connect(String url, String playerName)  {
        websocketClientHandler.startClient(url, playerName);
    }

    /**
     * indicate the UI that the connection failed
     */
    public void connectionFailed(){
        startClientActivity.openConnectionFailedDialog();
    }

    /**
     * Returns the player who got killed in the current round
     *
     * @return the player object which got killed
     */
    public Player getPlayerKilledByWerewolfesName() {
        //Long id = Long.getLong(gameContext.getSetting(SettingsEnum.KILLED_BY_WEREWOLF));
        //String id = gameContext.getSetting(SettingsEnum.KILLED_BY_WEREWOLF);
        Long id = ContextUtil.lastKilledPlayerID;
        if (id != -1) {
            Log.d(TAG, "Werewolves killed: " + gameContext.getPlayerById(id).getPlayerName());
            return gameContext.getPlayerById(id);
        } else {
            Log.d(TAG, "Werewolves killed no one this round");
            return null;
        }
    }

    /**
     * Returns the player who got killed by the witch in the current round
     *
     * @return the player object which got killed by the witch
     */
    public Player getPlayerKilledByWitchName() {
        Long id = ContextUtil.lastKilledPlayerIDByWitch;
        if (id != -1) {
            Log.d(TAG, "Witch killed: " + gameContext.getPlayerById(id).getPlayerName());
            return gameContext.getPlayerById(id);
        } else {
            Log.d(TAG, "Witch killed no one this round");
            return null;
        }
    }

    /**
     * Sends a package to the Server, indicating that the Client is done, and waiting
     * for the others to go to the next phase.
     */
    public void sendDoneToServer() {
        // if not the host
        if (myId != 0) {
            try {
                NetworkPackage<GamePhaseEnum> np = new NetworkPackage<>(NetworkPackage.PACKAGE_TYPE.DONE);
                websocketClientHandler.send(np);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (myId == 0) {
            Log.d(TAG, "Host is now done!");
            ServerGameController.HOST_IS_DONE = true;
            // startNextPhase when all Clients are ready as well
            if (ServerGameController.CLIENTS_ARE_DONE) {
                serverGameController.startNextPhase();
            }
        }
    }

    public GameActivity getGameActivity() {
        return gameActivity;
    }

    public void setGameActivity(GameActivity gameActivity) {
        this.gameActivity = gameActivity;
    }

    public StartClientActivity getStartClientActivity() {
        return startClientActivity;
    }

    public void setStartClientActivity(StartClientActivity startClientActivity) {
        this.startClientActivity = startClientActivity;
    }

    public WebsocketClientHandler getWebsocketClientHandler() {
        return websocketClientHandler;
    }

    public void setWebsocketClientHandler(WebsocketClientHandler websocketClientHandler) {
        this.websocketClientHandler = websocketClientHandler;
    }

    public GameContext getGameContext() {
        return gameContext;
    }

    public Player getMyPlayer() {
        return gameContext.getPlayerById(myId);
    }

    public void setMe(Player me) {
        this.me = me;
    }

    public long getMyPlayerId() {
        return myId;
    }

    public void setMyId(long myId) {
        this.myId = myId;
    }

    public void updateMe() {
        this.me = gameContext.getPlayerById(this.myId);
        Log.d(TAG, "Me is now: " + me.getPlayerName() + "  isDead?: " + me.isDead());
    }

    public void setPhase(GamePhaseEnum phase) {
        gameContext.setCurrentPhase(phase);
    }

    public void setServerGameController() {
        serverGameController = ServerGameController.getInstance();
    }

    public List<Player> getPlayerList() {
        return gameContext.getPlayersList();
    }

    public void showSuccesfulConnection() {
        if (myId != 0) {
            this.startClientActivity.showConnected();
        }
    }

    // read from the settings, if background music is enabled
    private boolean getBackgroundMusicSetting() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.getContextOfApplication());
        return sharedPref.getBoolean(Constants.pref_sound_background, true);
    }

    /**
     * Either called when the Host ended the game,
     * or the Client pressed the Back-button and confirmed
     */
    public void abortGame() {
        gameActivity.stopGameThread();
        destroy();

        // go back to start screen
        gameActivity.goToMainActivity();
    }

    /**
     * Destroy all game data and reset to 0.
     * After this you are able to start a new game without any old data
     */
    public void destroy() {

        gameContext.destroy();
        ContextUtil.destroy();
        if(websocketClientHandler != null) {
            websocketClientHandler.destroy();
        }
        // if I am the host
        if (serverGameController != null) {
            serverGameController.destroy();
        }
        me = new Player();
        System.gc();
    }
}
