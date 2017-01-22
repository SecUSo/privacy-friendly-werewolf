package org.secuso.privacyfriendlywerwolf.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.controller.GameController;
import org.secuso.privacyfriendlywerwolf.controller.GameControllerImpl;
import org.secuso.privacyfriendlywerwolf.model.Player;
import org.secuso.privacyfriendlywerwolf.server.ServerGameController;

public class GameActivity extends BaseActivity {
    ArrayList<Player> players;
    //ServerGameController serverGameController;
    GameController gameController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // TODO: muss später weg. gutes Beispiel was bei Kippen des Bildschirm passiert
        Toast.makeText(GameActivity.this, "Welcome to Werewolf", Toast.LENGTH_SHORT).show();

        Intent intent = getIntent();
        // players = intent.getStringArrayListExtra(LobbyActivity.PLAYERS_MESSAGE);
        players = (ArrayList<Player>) intent.getSerializableExtra(LobbyActivity.PLAYERS_MESSAGE);

        gameController = GameControllerImpl.getInstance();
        gameController.setGameActivity(this);


        // Ausgabe Test
        GridLayout layout = (GridLayout) findViewById(R.id.players);
        Button example_button = (Button) findViewById(R.id.example_button);
        ViewGroup.LayoutParams button_layout = example_button.getLayoutParams();
        layout.removeView(example_button);

        for(int i=0; i<players.size();i++) {

            Button button = new Button(this);
            button.setText(players.get(i).getName());
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

        } // Ausgabe Test Ende
    }


    public void outputMessage(final String message) {
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



}
