package org.secuso.privacyfriendlywerwolf.activity;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import org.secuso.privacyfriendlywerwolf.dialog.WitchDialog;
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

    public static final int ELIXIR_CLICK = 0;
    public static final int POISON_CLICK = 1;
    private static final String TAG = "GameActivity";

    List<Player> players;
    List<Button> playerButtons;
    PlayerAdapter playerAdapter;
    Handler gameHandler;
    Looper gameLooper;

    // this is important
    ClientGameController gameController;
    ServerGameController serverGameController;

    TextView messageView;
    CountDownTimer countDownTimer;
    boolean isHost;

    public FloatingActionButton fab;
    private MediaPlayer mediaPlayer;

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

        HandlerThread gameThread = new HandlerThread("Game Thread");
        gameThread.start();
        gameLooper = gameThread.getLooper();
        gameHandler = new Handler(gameLooper);


        playerButtons = new ArrayList<>();
        gameController = ClientGameController.getInstance();
        gameController.setGameActivity(this);

        players = GameContext.getInstance().getPlayersList();

        messageView = (TextView) findViewById(R.id.message);

        // don't turn off the screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // add action bar with some game options
        ActionBar actionBar = getSupportActionBar();


        if (isHost) {
            mediaPlayer = MediaPlayer.create(this, R.raw.game_start);
            mediaPlayer.start();

            outputMessage(R.string.progressBar_initial);
            //longOutputMessage(R.string.gameStart_start);
            longOutputMessage(R.string.gameStart_hintRoles);

            fab = (FloatingActionButton) findViewById(R.id.next_fab);
            fab.setVisibility(View.VISIBLE);


            // if all players are connected the host can start the game
            // by clicking the start_game_button
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gameHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ServerGameController.HOST_IS_DONE = true;
                            ServerGameController.CLIENTS_ARE_DONE = true;
                            //fab.setVisibility(View.INVISIBLE);
                            deactivateNextButton();
                            ServerGameController.getInstance().startNextPhase();
                        }
                    });
                }
            });

            fab.setClickable(false);

            gameHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    activateNextButton();
                }
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

    public void showTextPopup(int titleInt, final String message) {
        final String title = getResources().getString(titleInt);
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


    public void showTextPopup(int titleInt, int messageInt, final String extra) {
        final String title = getResources().getString(titleInt);
        final String message = getResources().getString(messageInt);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextDialog textDialog = new TextDialog();
                textDialog.setDialogTitle(title);
                textDialog.setDialogText(message + " " + extra);
                textDialog.show(getFragmentManager(), "textPopup");
            }
        });

    }

    public void showWitchTextPopup(final String title, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextDialog textDialog = new TextDialog();
                textDialog.setDialogText(message);
                textDialog.setDialogTitle(title);
                textDialog.setMargin(0.15F);
                textDialog.show(getFragmentManager(), "textPopup");
            }
        });

    }


    public void showWitchElixirPopup(int titleInt, int messageInt) {
        final String title = getResources().getString(titleInt);
        final String message = getResources().getString(messageInt);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WitchDialog textDialog = WitchDialog.newInstance(0);
                textDialog.setDialogText(message);
                textDialog.setDialogTitle(title);
                textDialog.show(getFragmentManager(), "textPopup");
            }
        });
    }

    public void showWitchElixirPopup(int titleInt, final String message) {
        final String title = getResources().getString(titleInt);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WitchDialog textDialog = WitchDialog.newInstance(0);
                textDialog.setDialogText(message);
                textDialog.setDialogTitle(title);
                textDialog.show(getFragmentManager(), "textPopup");
            }
        });
    }

    public void showWitchElixirPopup(final String title, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WitchDialog textDialog = WitchDialog.newInstance(0);
                textDialog.setDialogText(message);
                textDialog.setDialogTitle(title);
                textDialog.show(getFragmentManager(), "textPopup");
            }
        });
    }

    public void showWitchPoisonPopup(int titleInt, int messageInt) {
        final String title = getResources().getString(titleInt);
        final String message = getResources().getString(messageInt);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WitchDialog textDialog = WitchDialog.newInstance(1);
                textDialog.setDialogText(message);
                textDialog.setDialogTitle(title);
                textDialog.show(getFragmentManager(), "textPopup");
            }
        });
    }

    public void showWitchPoisonPopup(int titleInt, final String message) {
        final String title = getResources().getString(titleInt);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WitchDialog textDialog = WitchDialog.newInstance(1);
                textDialog.setDialogText(message);
                textDialog.setDialogTitle(title);
                textDialog.show(getFragmentManager(), "textPopup");
            }
        });
    }

    public void showWitchPoisonPopup(final String title, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WitchDialog textDialog = WitchDialog.newInstance(1);
                textDialog.setDialogText(message);
                textDialog.setDialogTitle(title);
                textDialog.show(getFragmentManager(), "textPopup");
            }
        });
    }


    public void doPositiveClick(int i) {
        if (i == ELIXIR_CLICK) {
            ClientGameController.getInstance().usedElixir();
            // TODO: give some feedback that it worked
            // TODO: end witch_elixir_phase
            gameHandler.post(new Runnable() {
                @Override
                public void run() {
                    gameController.endWitchElixirPhase();
                }
            });

        } else if (i == POISON_CLICK) {
            outputMessage("Choose a card!");
        } else {
            Log.d(TAG, "Something went wrong in WitchDialog");
        }
    }

    public void doNegativeClick(int i) {
        if (i == ELIXIR_CLICK) {
            gameHandler.post(new Runnable() {
                @Override
                public void run() {
                    gameController.endWitchElixirPhase();
                }
            });

            //ClientGameController.getInstance().usePoison();
        } else if (i == POISON_CLICK) {
            gameHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    longOutputMessage("Close your eyes!");

                    gameController.endWitchPoisonPhase();
                }
            }, 1000);

            //ClientGameController.getInstance().endWitchPhase();
        } else {
            Log.d(TAG, "Something went wrong in WitchDialog");
        }
    }

    public void askWitchForElixir() {
        showWitchElixirPopup(R.string.gamefield_witch_elixir_action, getString(R.string.gamefield_witch_elixir_action_message2));
    }


    public void askWitchForPoison() {
        showWitchPoisonPopup(R.string.gamefield_witch_poison_action, R.string.gamefield_witch_poison_action_message);
    }

    public void showYesNoBox(final int icon, final int title, final int message, final DialogInterface.OnClickListener yesAction, final DialogInterface.OnClickListener noAction) {


    }

    // make the NextButton clickable
    public void activateNextButton() {
        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent)));
                              fab.setClickable(true);
                          }
                      }

        );
    }

    // make the NextButton unclickable
    public void deactivateNextButton() {
        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.middlegrey)));
                              fab.setClickable(false);
                          }
                      }

        );
    }

    public void outputMessage(final String message) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                messageView.setText(message);
            }
        });
    }

    public void outputMessage(int messageInt) {
        final String message = this.getResources().getString(messageInt);
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                messageView.setText(message);
            }
        });

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

    public void longOutputMessage(int messageInt, final String extra) {
        final String message = getResources().getString(messageInt);
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message + " " + extra, Toast.LENGTH_LONG).show();
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
     * Updates the visible player cards on the gamefield
     */
    public void updateGamefield() {
        final GameActivity gameActivity = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GridView layout = (GridView) findViewById(R.id.players);
                playerAdapter = new PlayerAdapter(gameActivity, gameController.getMyPlayerId());
                layout.setAdapter(playerAdapter);
            }
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
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                serverGameController.abortGame();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(R.drawable.ic_close_black_24dp)
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
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        getApplicationContext().startActivity(intent);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(R.drawable.ic_close_black_24dp)
                .show();
    }

    @Override
    protected void onDestroy() {
        gameHandler.removeCallbacksAndMessages(null);
        gameLooper.quit();
        super.onDestroy();
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }


    public void runOnGameThread(final Runnable r, final long delay) {
        if (gameHandler != null) {
            if (delay >= 0) {
                gameHandler.postDelayed(r, delay);
            } else {
                gameHandler.post(r);
            }
        }
    }


    public FloatingActionButton getNextButton() {
        return fab;
    }
}
