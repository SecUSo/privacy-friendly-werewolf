package org.secuso.privacyfriendlywerwolf.activity;


import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.model.Player;

public class LobbyActivity extends ListActivity {

    public final static String PLAYERS_MESSAGE = "secuso.org.privacyfriendlywerwolf.PLAYERS";
    private ArrayList<Player> players;
    //just for now
    //TODO: use custom Player Adapter !!!!
    private ArrayList<String> stringPlayers;
    private ArrayAdapter<String> playerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        players = new ArrayList<>();
        stringPlayers = new ArrayList<>();
        playerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stringPlayers);
        setListAdapter(playerAdapter);
        ListView listView = getListView();
        listView.setTextFilterEnabled(true);
    }

    public void addPlayer(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.popup_title_addPlayer);

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Player player = new Player();
                player.setName(input.getText().toString());
                players.add(player);
                //just for now
                stringPlayers.add(input.getText().toString());
                playerAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(R.string.button_decline, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void startGame(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(PLAYERS_MESSAGE, players);
        startActivity(intent);
    }
}
