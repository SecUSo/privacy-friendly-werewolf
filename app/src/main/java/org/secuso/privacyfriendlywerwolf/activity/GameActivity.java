package org.secuso.privacyfriendlywerwolf.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.adapter.PlayerAdapter;
import org.secuso.privacyfriendlywerwolf.client.ClientGameController;
import org.secuso.privacyfriendlywerwolf.context.GameContext;
import org.secuso.privacyfriendlywerwolf.dialog.TextDialog;
import org.secuso.privacyfriendlywerwolf.dialog.VotingDialog;
import org.secuso.privacyfriendlywerwolf.model.Player;
import org.secuso.privacyfriendlywerwolf.server.ServerGameController;

import java.util.ArrayList;
import java.util.List;

/**
 * Game activity is the game field to render the game on the screen
 *
 * @author Florian Staubach <florian.staubach@stud.tu-darmstadt.de>
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class GameActivity extends BaseActivity {

    List<Player> players;
    List<Button> playerButtons;
    PlayerAdapter playerAdapter;

    // this is important
    ClientGameController gameController;

    ServerGameController serverGameController;

    TextView messageView;
    CountDownTimer countDownTimer;

    /**
     * Let's start a new activity to start the game
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        boolean isHost = intent.getBooleanExtra("Host", false);

        playerButtons = new ArrayList<>();
        gameController = ClientGameController.getInstance();
        gameController.setGameActivity(this);

        players = GameContext.getInstance().getPlayersList();

        messageView = (TextView) findViewById(R.id.message);

        // don't turn off the screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // with this the GameHostActivity is not needed anymore
        if(isHost) {
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.next_fab);
            fab.setVisibility(View.VISIBLE);


            // if all players are connected the host can start the game
            // by clicking the start_game_button
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GameContext.Phase nextRound = ServerGameController.getInstance().startNextPhase();

                }
            });

            serverGameController = ServerGameController.getInstance();
            serverGameController.setGameActivity(this);
            //ServerGameController.getInstance().setGameActivity(this);
            gameController.setServerGameController();
        }

        updateGamefield();
    }

    public void openVoting() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                VotingDialog votingDialog = new VotingDialog();
                votingDialog.show(getFragmentManager(), "voting");
                updateGamefield();
            }
        });

    }


    public void showTextPopup(final String title, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextDialog textDialog = new TextDialog();
                textDialog.setDialogText(message);
                textDialog.setDialogTitle(title);
                textDialog.show(getFragmentManager(), "textPopup");
                updateGamefield();
            }
        });

    }

    public void showTextPopup(int titleInt, int messageInt) {
        final String title = getResources().getString(titleInt);
        final String message = getResources().getString(messageInt);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextDialog textDialog = new TextDialog();
                textDialog.setDialogText(message);
                textDialog.setDialogTitle(title);
                textDialog.show(getFragmentManager(), "textPopup");
                updateGamefield();
            }
        });

    }

    public void outputMessage(String message) {
        this.messageView.setText(message);
    }

    public void outputMessage(int message) {
        this.messageView.setText(this.getResources().getString(message));
    }

    public void longOutputMessage(final String message) {
        // accessing UI thread from background thread
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void longOutputMessage(int messageInt) {
        final String message = getResources().getString(messageInt);
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void showElixirs() {
        // TODO: make the healing potion and the poisoned potion visible (use buttons)
        // make buttons gray depending if already used or not, also use output message
        // depending on potion usage
    }

    /**
     * Creates a timer on the view
     *
     * @param seconds the time in seconds
     * @return a CountDownTimer object able to be started
     */
    public CountDownTimer makeTimer(int seconds) {

        if (this.countDownTimer != null) {
            this.countDownTimer.cancel();
        }

        // get objects from view
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        final TextView countdown = (TextView) findViewById(R.id.countdown);

        progressBar.setMax(seconds * 1000);

        this.countDownTimer = new CountDownTimer(seconds * 1000, 1000) {

            ClientGameController gameController = ClientGameController.getInstance();

            /**
             * Update progress bar and time on regular interval.
             *
             * @param millisUntilFinished The amount of time until finished.
             */
            @Override
            public void onTick(long millisUntilFinished) {
                long progress = millisUntilFinished;

                ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", Long.valueOf(progress).intValue());
                animation.setDuration(999); // 0.5 second
                animation.setInterpolator(new DecelerateInterpolator());
                animation.start();

                // progressBar.setProgress(Long.valueOf(progress).intValue());
                countdown.setText(Long.valueOf(progress / 1000).toString() + " s");
            }

            /**
             * Callback fired when the time is up.
             */
            @Override
            public void onFinish() {
                //TODO: trigger something here
                countdown.setText("---");
                progressBar.setProgress(0);
            }
        };

        return this.countDownTimer;
    }

    public void updateGamefield() {
        GridView layout = (GridView) findViewById(R.id.players);
        playerAdapter = new PlayerAdapter(this, gameController.getMyPlayerId());
        layout.setAdapter(playerAdapter);

    }
}
