package org.secuso.privacyfriendlywerwolf.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import org.secuso.privacyfriendlywerwolf.R;

public class GameHostActivity extends GameActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_game);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.game_fab);
        fab.setVisibility(View.VISIBLE);

        // if all players are connected the host can start the game
        // by clicking the start_game_button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO switch to next state
            }
        });
    }


}
