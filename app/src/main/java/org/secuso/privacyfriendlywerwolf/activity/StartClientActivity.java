package org.secuso.privacyfriendlywerwolf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.client.WebsocketClientHandler;
import org.secuso.privacyfriendlywerwolf.controller.GameController;
import org.secuso.privacyfriendlywerwolf.controller.GameControllerImpl;
import org.secuso.privacyfriendlywerwolf.helpers.PermissionHelper;
import org.secuso.privacyfriendlywerwolf.model.Player;

import java.util.ArrayList;

/**
 * StartClientActivity is the default page to start a game
 * The user can enter a local ip address to connect to an open server or can start to create one
 *
 * @author Florian Staubach <florian.staubach@stud.tu-darmstadt.de>
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class StartClientActivity extends BaseActivity {

    TextView textResponse;
    EditText editTextAddress, editTextPlayerName;
    Button buttonConnect, buttonClear;
    Toolbar toolbar;
    public final static String PLAYERS_MESSAGE = "secuso.org.privacyfriendlywerwolf.PLAYERS";
    private ArrayList<Player> players;
    //just for now
    //TODO: use custom Player Adapter !!!!
    private ArrayList<String> stringPlayers;
    private ArrayAdapter<String> playerAdapter;
    GameController gameController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_client);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle(R.string.joingame_subtitle);

        editTextAddress = (EditText) findViewById(R.id.address);
        //editTextPort = (EditText) findViewById(R.id.port);
        editTextPlayerName = (EditText) findViewById(R.id.playerName);
        buttonConnect = (Button) findViewById(R.id.connect);
        buttonClear = (Button) findViewById(R.id.clear);
        textResponse = (TextView) findViewById(R.id.response);

        gameController = GameControllerImpl.getInstance();


        buttonConnect.setOnClickListener(new OnClickListener() {



            @Override
            public void onClick(View arg0) {
                String url = editTextAddress.getText().toString();
                String playerName = editTextPlayerName.getText().toString();
                gameController.connect("ws://" + url + ":5000/ws", playerName);
                //disable on connect, so no duplicate connections
                buttonConnect.setEnabled(false);
                //TODO: Render new text "Wait for the host to start the game"
            }
        });

        buttonClear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                textResponse.setText("");
            }
        });


        PermissionHelper.showWifiAlert(this);

    }

    public void startGame(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(PLAYERS_MESSAGE, players);
        startActivity(intent);
    }

}
