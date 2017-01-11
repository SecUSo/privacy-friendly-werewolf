package org.secuso.privacyfriendlywerwolf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.client.WebsocketClientHandler;
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
    EditText editTextAddress, editTextPort;
    Button buttonConnect, buttonClear;
    Toolbar toolbar;
    WebsocketClientHandler websocketClientHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_client);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle(R.string.joingame_subtitle);

        editTextAddress = (EditText) findViewById(R.id.address);
        editTextPort = (EditText) findViewById(R.id.port);
        buttonConnect = (Button) findViewById(R.id.connect);
        buttonClear = (Button) findViewById(R.id.clear);
        textResponse = (TextView) findViewById(R.id.response);

        buttonConnect.setOnClickListener(buttonConnectOnClickListener);

        buttonClear.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                textResponse.setText("");
            }
        });
        websocketClientHandler = new WebsocketClientHandler();

        PermissionHelper.showWifiAlert(this);
    }

    OnClickListener buttonConnectOnClickListener =
            new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                   websocketClientHandler.startClient("ws://192.168.0.22:5000/ws");
                  //  ws://192.168.0.22:5000/ws"
                    // MyClientTask myClientTask = new MyClientTask(
                    //       editTextAddress.getText().toString(),
                    //     Integer.parseInt(editTextPort.getText().toString()));
                    //myClientTask.execute();
                }
            };


    public void startServer(View view) {
        Intent intent = new Intent(this, StartHostActivity.class);
        startActivity(intent);
    }

}
