package org.secuso.privacyfriendlywerwolf.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.context.GameContext;
import org.secuso.privacyfriendlywerwolf.server.ServerGameController;

/**
 * There is always one player who is the host of the game. He has special functions
 * which need to be defined
 *
 * @author Florian Staubach <florian.staubach@stud.tu-darmstadt.de>
 */
public class GameHostActivity extends GameActivity {

    ServerGameController serverGameController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_game);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.next_fab);
        fab.setVisibility(View.VISIBLE);

        // if all players are connected the host can start the game
        // by clicking the start_game_button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameContext.Phase nextRound = serverGameController.startNextPhase();
            }
        });

        serverGameController = serverGameController.getInstance();
        //serverGameController.setGameHostActivity(this);

    }

    @Override
    protected void onDestroy() {
        serverGameController.destroy();
        super.onDestroy();
    }


}
