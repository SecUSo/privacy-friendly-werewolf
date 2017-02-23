package org.secuso.privacyfriendlywerwolf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.client.ClientGameController;
import org.secuso.privacyfriendlywerwolf.helpers.PermissionHelper;

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
    ClientGameController gameController;


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

        gameController = ClientGameController.getInstance();
        gameController.setStartClientActivity(this);


        buttonConnect.setOnClickListener(new OnClickListener() {



            @Override
            public void onClick(View arg0) {
                String url = editTextAddress.getText().toString();
                String playerName = editTextPlayerName.getText().toString();
                gameController.connect("ws://" + url + ":5000/ws", playerName);
                // disable on connect, so no duplicate connections
                //TODO: make button grey, if disabled
               // buttonConnect.setEnabled(false);
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

    public void startGame() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    /**
     * If you are connected to the server successfully remove all elements and inform the user
     */
    public void showConnected() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout layout = (LinearLayout) findViewById(R.id.connectForm);
                layout.removeAllViews();

                TextView waitMessage = new TextView(getApplicationContext());
                waitMessage.setText(R.string.joingame_connected);
                waitMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                waitMessage.setTextSize(TypedValue.COMPLEX_UNIT_PT, 10f);
                waitMessage.setPadding(0, 50, 0, 0);
                waitMessage.setTextColor(getResources().getColor(R.color.black));

                layout.addView(waitMessage);
            }
        });

    }

}
