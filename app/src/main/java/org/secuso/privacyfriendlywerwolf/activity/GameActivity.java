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

import java.util.ArrayList;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.model.Player;

public class GameActivity extends BaseActivity {
    ArrayList<Player> players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        // players = intent.getStringArrayListExtra(LobbyActivity.PLAYERS_MESSAGE);
        players = (ArrayList<Player>) intent.getSerializableExtra(LobbyActivity.PLAYERS_MESSAGE);


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
                            .setTitle("This is a player's card")
                            .setMessage("This is a player's card in the game. You cannot reveal the character until you died or with special power.")
                            .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                             })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });

            layout.addView(button);

        } // Ausgabe Test Ende
    }
}
