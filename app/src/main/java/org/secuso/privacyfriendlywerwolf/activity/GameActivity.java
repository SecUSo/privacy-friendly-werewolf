package org.secuso.privacyfriendlywerwolf.activity;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.Thing;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.context.GameContext;
import org.secuso.privacyfriendlywerwolf.client.ClientGameController;
import org.secuso.privacyfriendlywerwolf.controller.GameController;
import org.secuso.privacyfriendlywerwolf.model.Player;

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

    // this is important
    GameController gameController;

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
        playerButtons = new ArrayList<>();
        gameController = ClientGameController.getInstance();
        gameController.setGameActivity(this);

        players = GameContext.getInstance().getPlayersList();

        messageView = (TextView) findViewById(R.id.message);

        // Ausgabe Test
        GridLayout layout = (GridLayout) findViewById(R.id.players);
        Button example_button = (Button) findViewById(R.id.example_button);
        ViewGroup.LayoutParams button_layout = example_button.getLayoutParams();
        layout.removeView(example_button);


        //TODO: DANIEL: use playeradapter instead of this shit
        for (int i = 0; i < players.size(); i++) {

            Button button = new Button(this);
            button.setText(players.get(i).getPlayerName());
            button.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
            button.setMinimumHeight(340);
            button.setMinimumWidth(340);
            button.setBackgroundResource(R.mipmap.app_icon);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(view.getContext())
                            .setTitle(R.string.gamefield_player_card)
                            .setMessage(R.string.gamefield_player_card_message)
                            .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(R.drawable.ic_face_black_24dp)
                            .show();
                }
            });

            layout.addView(button);
            playerButtons.add(button);

        } // Ausgabe Test Ende
    }

    public void openVoting() {
        VotingDialog votingDialog = new VotingDialog();
        votingDialog.show(getFragmentManager(), "voting");

    }

    public void renderButtons() {
        //TODO: render buttons, and new icons
        for (Button playerButton : playerButtons) {
            Player player = GameContext.getInstance().getPlayerByName(playerButton.getText().toString());
            if (player.isDead()) {
                runOnUiThread(new Runnable() {
                    Button playerButton;

                    private Runnable init(Button button) {
                        playerButton = button;
                        return this;
                    }

                    @Override
                    public void run() {
                        //TODO: set a new icon!
                        playerButton.setText("TOT !!!");
                        playerButton.invalidate();
                    }
                }.init(playerButton));


            }
        }
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

    public void showElixirs() {
        // TODO: make the healing potion and the poisoned potion visible (use buttons)
        // make buttons gray depending if already used or not, also use output message
        // depending on potion usage
    }

    /**
     * Creates a timer on the view
     * @param seconds the time in seconds
     * @return a CountDownTimer object able to be started
     */
    public CountDownTimer makeTimer(int seconds) {

        if(this.countDownTimer != null) {
            this.countDownTimer.cancel();
        }

        // get objects from view
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        final TextView countdown = (TextView) findViewById(R.id.countdown);

        progressBar.setMax(seconds * 1000);

        this.countDownTimer = new CountDownTimer(seconds * 1000, 1000) {

            GameController gameController = ClientGameController.getInstance();

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


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Game Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }


}
