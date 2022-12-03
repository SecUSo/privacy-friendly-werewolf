package org.secuso.privacyfriendlywerwolf.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.client.ClientGameController;
import org.secuso.privacyfriendlywerwolf.dialog.TextDialog;
import org.secuso.privacyfriendlywerwolf.helpers.PermissionHelper;

import java.util.Random;

import static org.secuso.privacyfriendlywerwolf.R.id.playerName;
import static org.secuso.privacyfriendlywerwolf.util.Constants.pref_playerName;

/**
 * StartClientActivity is the default page to start a game
 * The user can enter a local ip address to connect to an open server or can start to create one
 *
 * @author Florian Staubach <florian.staubach@stud.tu-darmstadt.de>
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class StartClientActivity extends BaseActivity {
    /**
     * view's
     */
    private TextView textResponse;
    private EditText editTextAddress, editTextPlayerName;
    private Button buttonConnect;
    private ProgressBar loadingIndicator;
    private Toolbar toolbar;

    /**
     * controller
     */
    private ClientGameController gameController;
    private boolean waitingMode;

    /**
     * preferences
     */
    private SharedPreferences sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.getContextOfApplication());
        String playerNameFromPref = sharedPref.getString(pref_playerName, "");
        setContentView(R.layout.activity_start_client);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setSubtitle(R.string.joingame_subtitle);

        editTextAddress = findViewById(R.id.address);
        editTextPlayerName = findViewById(playerName);
        editTextPlayerName.setText(playerNameFromPref);
        buttonConnect = findViewById(R.id.connect);
        textResponse = findViewById(R.id.connecting);
        loadingIndicator = findViewById(R.id.loading_indicator);


        gameController = ClientGameController.getInstance();
        gameController.setStartClientActivity(this);

        waitingMode = false;

        // connect to the host
        buttonConnect.setOnClickListener(arg0 -> {
            String url = editTextAddress.getText().toString();
            String playerName = editTextPlayerName.getText().toString();
            sharedPref.edit().putString(pref_playerName, playerName).apply();

            if(TextUtils.isEmpty(editTextPlayerName.getText().toString())) {
                playerName = getString(R.string.player_name_default) + " " + new Random().nextInt(1000);
            }

            gameController.connect("ws://" + url + ":5000/ws", playerName);
            deactivateConnectButton();
        });

        PermissionHelper.showWifiAlert(this);

    }

    /**
     * Makes the Connect button clickable again. Dismisses loading indicator.
     */
    public void activateConnectButton() {
        buttonConnect.setClickable(true);
        buttonConnect.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));
        textResponse.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.GONE);
    }

    /**
     * Makes the Connect button unclickable. Shows loading indicator.
     */
    public void deactivateConnectButton() {
        buttonConnect.setClickable(false);
        buttonConnect.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.middlegrey)));
        textResponse.setVisibility(View.VISIBLE);
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    /**
     * shows the connection failed dialog (due to timeoutissues, this can be delayed,
     * therefore wait, if the user changed this fast)
     */
    public void openConnectionFailedDialog() {
        if (!waitingMode) {
            TextDialog textDialog = new TextDialog();
            textDialog.setDialogTitle(getResources().getString(R.string.uh_dialog_title));
            textDialog.setDialogText(getResources().getString(R.string.uh_dialog_text));
            textDialog.show(getSupportFragmentManager(), "unknownHostDialog");
            activateConnectButton();
        }
    }

    /**
     * start the GameActivity
     */
    public void startGame() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    /**
     * If you are connected to the server successfully remove all elements and inform the user
     */
    public void showConnected() {
        waitingMode = true;
        runOnUiThread(() -> {
            LinearLayout layout = findViewById(R.id.connectForm);
            layout.removeAllViews();

            TextView waitMessage = new TextView(getApplicationContext());
            waitMessage.setText(R.string.joingame_connected);
            waitMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            waitMessage.setTextSize(TypedValue.COMPLEX_UNIT_PT, 10f);
            waitMessage.setPadding(0, 50, 0, 0);
            waitMessage.setTextColor(getResources().getColor(R.color.black));

            layout.addView(waitMessage);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.handlePermissionRequestResult(this, requestCode, permissions, grantResults);
    }
}
