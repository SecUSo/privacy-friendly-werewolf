package org.secuso.privacyfriendlywerwolf.activity;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.Thing;

import org.secuso.privacyfriendlywerwolf.R;
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


        // Ausgabe Test
        GridLayout layout = (GridLayout) findViewById(R.id.players);

        layout.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        int width = layout.getMeasuredWidth() / 3;

        Button example_button = (Button) findViewById(R.id.example_button);
        GridLayout.LayoutParams button_layout = (GridLayout.LayoutParams) example_button.getLayoutParams();

        layout.removeView(example_button);
        layout.setColumnCount(3);


        //TODO: DANIEL: use playeradapter instead of this shit
        for (Player player : players) {

            Button button = new Button(this);
            button.setText(player.getPlayerName());
            button.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

            int dim = (int) getResources().getDimension(R.dimen.player_button);
            // button.setMinimumHeight(width);
            // button.setMinimumWidth(width);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(dim, dim);
            // layoutParams.setGravity(GridLayout.LayoutParams.MATCH_PARENT);
            // layoutParams.width = width;
            // layoutParams.height = width;
            button.setLayoutParams(layoutParams);

            // if this player is me, then use different color and behaviour
            if(player.isDead()) {
                button.setBackgroundResource(R.mipmap.player_button_dead);
                button.invalidate();
            }
            else if(gameController.getMyPlayerId() == player.getPlayerId()) {
                button.setBackgroundResource(R.mipmap.player_button_me);
                button.setId(R.id.player_button_me);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(view.getContext())
                                .setTitle(R.string.gamefield_your_player_card)
                                .setMessage(R.string.gamefield_your_player_card_message)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        String message = getResources().getString(R.string.gamefield_player_identity);
                                        message += getResources().getString(gameController.getMyPlayer().getPlayerRole().getRole());
                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .setIcon(R.drawable.ic_face_black_24dp)
                                .show();
                    }
                });
            } else {
                button.setBackgroundResource(R.mipmap.player_button);
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
            }

            layout.addView(button);
            playerButtons.add(button);

        } // Ausgabe Test Ende
    }

    public void openVoting() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                VotingDialog votingDialog = new VotingDialog();
                votingDialog.show(getFragmentManager(), "voting");
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
            }
        });

    }

    // TODO: use temporarily but needs improvement
    public void renderButtons() {

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
                        playerButton.setBackgroundResource(R.mipmap.player_button_dead);
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
