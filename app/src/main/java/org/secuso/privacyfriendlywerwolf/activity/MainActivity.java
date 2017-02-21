package org.secuso.privacyfriendlywerwolf.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.dialog.PlayerNameInputDialog;

/**
 * Starting activiy when the game is fully loaded and tutorial is passed
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

        Button buttonJoinGame = (Button) findViewById(R.id.game_button_join);
        Button buttonNewGame = (Button) findViewById(R.id.game_button_start);

        buttonJoinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinGame(view);
            }
        });

        buttonNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewGame(view);
            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.action_main);

    }

    public void startNewGame(View view){
        PlayerNameInputDialog playerNameInputDialog = new PlayerNameInputDialog();
        playerNameInputDialog.show(getFragmentManager(), "playerNameInputDialog");
    }

    public void joinGame(View view){
        Intent intent = new Intent(this, StartClientActivity.class);
        startActivity(intent);
    }

    public static Context getContextOfApplication(){
        return contextOfApplication;
    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_main;
    }
}
