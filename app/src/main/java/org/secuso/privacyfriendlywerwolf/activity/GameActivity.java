package org.secuso.privacyfriendlywerwolf.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.adapter.PlayerAdapter;
import org.secuso.privacyfriendlywerwolf.client.ClientGameController;
import org.secuso.privacyfriendlywerwolf.dialog.TextDialog;
import org.secuso.privacyfriendlywerwolf.dialog.VotingDialog;
import org.secuso.privacyfriendlywerwolf.dialog.WitchDialog;
import org.secuso.privacyfriendlywerwolf.enums.GamePhaseEnum;
import org.secuso.privacyfriendlywerwolf.server.ServerGameController;
import org.secuso.privacyfriendlywerwolf.util.Constants;
import org.secuso.privacyfriendlywerwolf.util.ContextUtil;
import org.secuso.privacyfriendlywerwolf.util.Screen;

/**
 * Game activity is the game field to render the game on the screen
 *
 * @author Florian Staubach <florian.staubach@stud.tu-darmstadt.de>
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class GameActivity extends BaseActivity {
    /**
     * statics
     */
    private static final int ELIXIR_CLICK = 0;
    private static final int POISON_CLICK = 1;
    private static final String TAG = "GameActivity";

    private PlayerAdapter playerAdapter;
    private Handler gameHandler;
    private Looper gameLooper;
    private HandlerThread gameThread;

    private boolean isHost = false;


    /**
     * controller
     */
    private ClientGameController gameController;
    private ServerGameController serverGameController;

    /**
     * view's
     */
    private TextView messageView;
    private CountDownTimer countDownTimer;
    public FloatingActionButton fab;
    private MediaPlayer mediaPlayer = null;
    private MediaPlayer backgroundPlayer = null;

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
        isHost = intent.getBooleanExtra("Host", false);

        gameThread = new HandlerThread("Game Thread");
        gameThread.start();
        gameLooper = gameThread.getLooper();
        gameHandler = new Handler(gameLooper);

        gameController = ClientGameController.getInstance();
        gameController.setGameActivity(this);

        messageView = (TextView) findViewById(R.id.message);

        // don't turn off the screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // add action bar with some game options
        ActionBar actionBar = getSupportActionBar();

        Screen.lockOrientation(this);


        if (isHost) {
            mediaPlayer = MediaPlayer.create(this, R.raw.game_start);
            mediaPlayer.start();

            outputMessage(R.string.progressBar_initial);
            longOutputMessage(R.string.gameStart_hintRoles);

            fab = findViewById(R.id.next_fab);
            fab.setVisibility(View.VISIBLE);


            // if all players are connected the host can start the game
            // by clicking the start_game_button
            fab.setOnClickListener(v -> gameHandler.post(() -> {
                ServerGameController.HOST_IS_DONE = true;
                ServerGameController.CLIENTS_ARE_DONE = true;
                if (ContextUtil.IS_FIRST_ROUND) {
                    Log.d(TAG, "FabButton clicked on Phase: " + gameController.getGameContext().getCurrentPhase().toString());

                    // at the end of the first round
                    if (ContextUtil.END_OF_ROUND && gameController.getGameContext().getCurrentPhase() != null
                            && (gameController.getGameContext().getCurrentPhase() == GamePhaseEnum.GAME_START)) {
                        // first round is over
                        ContextUtil.IS_FIRST_ROUND = false;
                    }
                    runOnUiThread(() -> {
                        // disable info text
                        TextView view = findViewById(R.id.fab_info_view);
                        view.setVisibility(View.GONE);
                    });

                }
                deactivateNextButton();
                ServerGameController.getInstance().startNextPhase();
            }));

            if (Constants.GAME_FEATURES_ACTIVATED) {
                fab.setClickable(false);
            }

            gameHandler.postDelayed(() -> {
                activateNextButton();
                showTextPopup("Ready, Set, Go!", R.string.popup_text_start_first_night);
                runOnUiThread(() -> this.<TextView>findViewById(R.id.fab_info_view).setVisibility(View.VISIBLE));
            }, 6000);

            serverGameController = ServerGameController.getInstance();
            serverGameController.setGameActivity(this);
            //ServerGameController.getInstance().setGameActivity(this);
            gameController.setServerGameController();
        }

        updateGamefield();

        Log.d(TAG, "Built screen with"
                + " density:" + getResources().getDisplayMetrics().density
                + " dpi:" + getResources().getDisplayMetrics().densityDpi
                + " scale:" + getResources().getDisplayMetrics().scaledDensity
                + " set:" + getPackageResourcePath());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (isHost) {
            getMenuInflater().inflate(R.menu.game_menu, menu);
        }
        return true;
    }


    /**
     * open the voting dialog
     */
    public void openVoting() {
        runOnUiThread(() -> {
            VotingDialog votingDialog = new VotingDialog();
            votingDialog.setCancelable(false);
            votingDialog.show(getSupportFragmentManager(), "voting");
        });

    }

    /**
     * shows a text popup with a title and a message
     *
     * @param title,   the title of the dialog
     * @param message, the message of the dialog
     */
    public void showTextPopup(final String title, final String message) {
        runOnUiThread(() -> {
            TextDialog textDialog = new TextDialog();
            textDialog.setDialogText(message);
            textDialog.setDialogTitle(title);
            textDialog.setCancelable(false);
            textDialog.show(getSupportFragmentManager(), "textPopup");
        });
    }

    /**
     * shows a text popup with a title and a message
     *
     * @param titleInt, the title of the dialog
     * @param message,  the message of the dialog
     */
    public void showTextPopup(int titleInt, final String message) {
        showTextPopup(getResources().getString(titleInt), message);
    }

    /**
     * shows a text popup with a title and a message
     *
     * @param title,   the title of the dialog
     * @param messageInt, the message of the dialog, coded in string.xml
     */
    public void showTextPopup(final String title, int messageInt, Object... messageArgs) {
        showTextPopup(title, getResources().getString(messageInt, messageArgs));
    }

    /**
     * shows a text popup with a title and a message
     *
     * @param titleInt,   the title of the dialog
     * @param messageInt, the message of the dialog
     */
    public void showTextPopup(int titleInt, int messageInt, Object... messageArgs) {
        showTextPopup(getResources().getString(titleInt), getResources().getString(messageInt, messageArgs));
    }

    /**
     * shows the endGame dialog
     *
     * @param titleInt, the title of the dialog
     */
    public void showGameEndTextView(int titleInt) {
        final String title = getResources().getString(titleInt);
        runOnUiThread(() -> {
            TextView view = findViewById(R.id.game_end_view);
            view.setText(title);
            view.setVisibility(View.VISIBLE);
        });
    }

    /**
     * shows the witch dialog
     *
     * @param titleInt,   the title of the dialog, coded in string.xml
     * @param message, the message of the dialog
     */
    public void showWitchTextPopup(int titleInt, final String message) {
        final String title = getResources().getString(titleInt);
        runOnUiThread(() -> {
            TextDialog textDialog = new TextDialog();
            textDialog.setDialogText(message);
            textDialog.setDialogTitle(title);
            textDialog.setMargin(0.15F);
            textDialog.setCancelable(false);
            textDialog.show(getSupportFragmentManager(), "textPopup");
        });
    }

    /**
     * shows the witch dialog
     */
    public void showWitchElixirPopup() {
        final String title = getResources().getString(R.string.gamefield_witch_elixir_action);
        final String message = getResources().getString(R.string.gamefield_witch_elixir_action_message2);
        runOnUiThread(() -> {
            WitchDialog textDialog = WitchDialog.newInstance(0);
            textDialog.setDialogText(message);
            textDialog.setDialogTitle(title);
            textDialog.setCancelable(false);
            textDialog.show(getSupportFragmentManager(), "textPopup");
        });
    }

    /**
     * shows the witch poison dialog
     */
    public void showWitchPoisonPopup() {
        final String title = getResources().getString(R.string.gamefield_witch_poison_action);
        final String message = getResources().getString(R.string.gamefield_witch_poison_action_message);
        runOnUiThread(() -> {
            WitchDialog textDialog = WitchDialog.newInstance(1);
            textDialog.setDialogText(message);
            textDialog.setDialogTitle(title);
            textDialog.setCancelable(false);
            textDialog.show(getSupportFragmentManager(), "textPopup");
        });
    }

    public void doPositiveClick(int i) {
        if (i == ELIXIR_CLICK) {
            ClientGameController.getInstance().usedElixir();
            gameHandler.post(() -> gameController.endWitchElixirPhase());

        } else if (i == POISON_CLICK) {
            outputMessage(R.string.progressBar_choose);
        } else {
            Log.d(TAG, "Something went wrong in WitchDialog");
        }
    }

    public void doNegativeClick(int i) {
        if (i == ELIXIR_CLICK) {
            gameHandler.post(() -> gameController.endWitchElixirPhase());
            //ClientGameController.getInstance().usePoison();
        } else if (i == POISON_CLICK) {
            gameHandler.postDelayed(() -> {
                longOutputMessage(R.string.toast_close_eyes);

                gameController.endWitchPoisonPhase();
            }, 1000);

            //ClientGameController.getInstance().endWitchPhase();
        } else {
            Log.d(TAG, "Something went wrong in WitchDialog");
        }
    }

    /**
     * shows little hint when and for what to press the next button
     *
     * @param info text to be displayed to the host
     */
    public void showFabInfo(final String info) {
        runOnUiThread(() -> {
            TextView view = findViewById(R.id.fab_info_view);
            view.setText(info);
            view.setVisibility(View.VISIBLE);
        });
    }

    /**
     * shows little hint when and for what to press the next button
     *
     * @param infoInt text to be displayed to the host, coded in string.xml
     */
    public void showFabInfo(int infoInt) {
        final String info = getResources().getString(infoInt);
        runOnUiThread(() -> {
            TextView view = findViewById(R.id.fab_info_view);
            view.setText(info);
            view.setVisibility(View.VISIBLE);
        });
    }

    // make the NextButton clickable
    public void activateNextButton() {
        runOnUiThread(() -> {
            fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent)));
            fab.setClickable(true);
        });
    }

    // make the NextButton unclickable
    public void deactivateNextButton() {
        runOnUiThread(() -> {
            fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.middlegrey)));
            if (Constants.GAME_FEATURES_ACTIVATED) {
                fab.setClickable(false);
            }
        });
    }

    public void outputMessage(final String message) {
        runOnUiThread(() -> messageView.setText(message));
    }

    public void outputMessage(int messageInt) {
        final String message = this.getResources().getString(messageInt);
        runOnUiThread(() -> messageView.setText(message));
    }

    public void longOutputMessage(final String message) {
        // accessing UI thread from background thread
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show());
    }

    public void longOutputMessage(int messageInt) {
        final String message = getResources().getString(messageInt);
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show());
    }

    public void longOutputMessage(int messageInt, final String extra) {
        final String message = getResources().getString(messageInt);
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), message + " " + extra, Toast.LENGTH_LONG).show());
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
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        final TextView countdown = findViewById(R.id.countdown);

        progressBar.setMax(seconds * 1000);

        this.countDownTimer = new CountDownTimer(seconds * 1000L, 1000) {

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
                countdown.setText((progress / 1000L) + " s");
            }

            /**
             * Callback fired when the time is up.
             */
            @Override
            public void onFinish() {
                countdown.setText("---");
                progressBar.setProgress(0);
            }
        };

        return this.countDownTimer;
    }

    /**
     * Updates the visible player cards on the gamefield
     */
    public void updateGamefield() {
        final GameActivity gameActivity = this;
        runOnUiThread(() -> {
            GridView layout = findViewById(R.id.players);
            playerAdapter = new PlayerAdapter(gameActivity, gameController.getMyPlayerId());
            layout.setAdapter(playerAdapter);
        });
    }

    /**
     * Called once there is a toolbar icon clicked
     *
     * @param item the item which was clicked
     * @return boolean if action was succesful
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_abort:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.gamefield_abort_game)
                        .setMessage(R.string.gamefield_abort_game_message)
                        .setPositiveButton(R.string.button_okay, (dialog, which) -> {
                            showTextPopup(R.string.popup_title_abort, R.string.popup_text_abort);
                            runOnGameThread(() -> serverGameController.abortGame(), 3000);

                        })
                        .setNegativeButton(android.R.string.no, (dialog, which) -> {
                            // do nothing
                        })
                        .setIcon(R.drawable.ic_close_black_24dp)
                        .setCancelable(false)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Is called once the hardware back button is clicked
     */
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.gamefield_press_back)
                .setMessage(R.string.gamefield_press_back_message)
                .setPositiveButton(R.string.button_okay, (dialog, which) -> {
                    showTextPopup(R.string.popup_title_abort, R.string.popup_text_abort);
                    runOnGameThread(() -> {
                        if (isHost) {
                            serverGameController.abortGame();
                        } else {
                            gameController.abortGame();
                        }
                    }, 3000);
                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                    // do nothing
                })
                .setIcon(R.drawable.ic_close_black_24dp)
                .setCancelable(false)
                .show();
    }

    public void goToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        // stop MediaPlayers if running
        if(mediaPlayer!=null) {
            if(mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        }
        if(backgroundPlayer!=null) {
            if(backgroundPlayer.isPlaying()) {
                backgroundPlayer.stop();
            }
        }
        stopGameThread();

        Screen.unlockOrientation(this);

        super.onDestroy();
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public MediaPlayer getBackgroundPlayer() { return backgroundPlayer; }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public void setBackgroundPlayer(MediaPlayer backgroundPlayer) { this.backgroundPlayer = backgroundPlayer; }


    /**
     * Post some tasks onto the GameThread
     * @param r the Runnable for the post
     * @param delay the delay if postDelayed used
     */
    public void runOnGameThread(final Runnable r, final long delay) {
        if (gameHandler != null) {
            if (delay >= 0) {
                gameHandler.postDelayed(r, delay);
            } else {
                gameHandler.post(r);
            }
        }
    }


    /**
     * Revoke any task on the GameThread
     */
    public void stopGameThread() {

        gameLooper.quit();
        gameHandler.removeCallbacksAndMessages(null);
    }


    public FloatingActionButton getNextButton() {
        return fab;
    }
}
