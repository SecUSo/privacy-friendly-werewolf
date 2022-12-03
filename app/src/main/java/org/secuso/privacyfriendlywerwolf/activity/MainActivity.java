package org.secuso.privacyfriendlywerwolf.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.dialog.PlayerInputDialog;

/**
 * Starting activity when the game is fully loaded and tutorial is passed
 *
 * @author Florian Staubach <florian.staubach@stud.tu-darmstadt.de>
 */
public class MainActivity extends BaseActivity {

    public static Context contextOfApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        contextOfApplication = getApplicationContext();
        setContentView(R.layout.activity_main);

        Button buttonJoinGame = findViewById(R.id.game_button_join);
        Button buttonNewGame = findViewById(R.id.game_button_start);

        buttonJoinGame.setOnClickListener(this::joinGame);

        buttonNewGame.setOnClickListener(this::startNewGame);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.action_main);

    }

    /**
     * start a new game as the host, showing an input dialog
     *
     * @param view
     */
    public void startNewGame(View view) {
        PlayerInputDialog playerInputDialog = new PlayerInputDialog();
        playerInputDialog.setCancelable(false);
        playerInputDialog.show(getSupportFragmentManager(), "playerInputDialog");
    }

    /**
     * start a new game as a client
     *
     * @param view
     */
    public void joinGame(View view) {
        Intent intent = new Intent(this, StartClientActivity.class);
        startActivity(intent);
    }

    /**
     * Returns the context of the application
     *
     * @return the context of the application
     */
    public static Context getContextOfApplication() {
        return contextOfApplication;
    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_main;
    }

    /**
     * Is called once the hardware back button is clicked
     */
    @Override
    public void onBackPressed() {
        finish();
    }
}
