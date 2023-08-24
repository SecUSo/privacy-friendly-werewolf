package org.secuso.privacyfriendlywerwolf.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.context.GameContext;
import org.secuso.privacyfriendlywerwolf.dialog.GameInformationDialog;
import org.secuso.privacyfriendlywerwolf.helpers.PermissionHelper;
import org.secuso.privacyfriendlywerwolf.model.Player;
import org.secuso.privacyfriendlywerwolf.server.ServerGameController;
import org.secuso.privacyfriendlywerwolf.util.Constants;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * StartHostActivity is the default page to start a game host
 * It waits for other clients to connect by creating a new Thread
 *
 * @author Florian Staubach <florian.staubach@stud.tu-darmstadt.de>
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class StartHostActivity extends BaseActivity {
    /**
     * views
     */
    private TextView infoip;
    private Toolbar toolbar;
    private Button buttonStart;
    private ArrayList<String> stringPlayers;
    private ArrayAdapter<String> playerAdapter;

    /**
     * statics
     */
    private static final String TAG = "StartHostActivity";
    private static final int MIN_PLAYER_COUNT = 6;

    /**
     * controller
     */
    private ServerGameController serverGameController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        serverGameController = serverGameController.getInstance();
        serverGameController.setStartHostActivity(this);
        //reset everything
        serverGameController.destroy();

        setContentView(R.layout.activity_start_host);
        infoip = (TextView) findViewById(R.id.infoip);

        infoip.setText(getIpAddress());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle(R.string.startgame_subtitle);

        PermissionHelper.showWifiAlert(this);


        // start the server
        serverGameController.startServer();


        buttonStart = (Button) findViewById(R.id.btn_start);


        // user clicks the button to start the game
        buttonStart.setOnClickListener(new View.OnClickListener() {
            StartHostActivity activity;

            private View.OnClickListener init(StartHostActivity activity) {
                this.activity = activity;
                return this;
            }

            @Override
            public void onClick(View view) {
                int players = serverGameController.getGameContext().getPlayersList().size();
                if (players >= MIN_PLAYER_COUNT) {
                    openGameInformationDialog();
                } else {
                    new AlertDialog.Builder(activity)
                            .setTitle(R.string.startgame_need_players)
                            .setMessage(R.string.startgame_need_players_message)
                            .setPositiveButton(R.string.button_okay, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    openGameInformationDialog();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // just close and wait for more players
                                }
                            })
                            .setIcon(R.drawable.ic_face_black_24dp)
                            .setCancelable(false)
                            .show();
                }

            }


        }.init(this));


        ListView list = (ListView) findViewById(R.id.host_player_list);


        stringPlayers = new ArrayList<>();
        fillStringPlayers();

        playerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stringPlayers);
        list.setAdapter(playerAdapter);
        Intent intent = getIntent();
        serverGameController.prepareServerPlayer(intent.getStringExtra(Constants.PLAYERNAME_PUTEXTRA));
    }

    private void openGameInformationDialog() {
        GameInformationDialog dialog = new GameInformationDialog();
        dialog.setAmountOfPlayers(stringPlayers.size());
        dialog.setStartHostActivity(this);
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), "gameInformationDialog");
    }

    /**
     * fill the lobby list
     */
    private void fillStringPlayers() {
        stringPlayers.clear();
        List<Player> players = GameContext.getInstance().getPlayersList();
        for (Player player : players) {
            stringPlayers.add(player.getPlayerName());
        }
    }

    /**
     * update the ui
     */
    public void renderUI() {
        fillStringPlayers();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerAdapter.notifyDataSetChanged();
            }
        });

    }

    /**
     * get the ip adress from the android framework
     *
     * @return the IP
     */
    private String getIpAddress() {
        if (!PermissionHelper.isWifiEnabled(this)) {
            return getResources().getString(R.string.text_view_enable_wifi);
        } else {
            String ip = "";
            try {
                Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                        .getNetworkInterfaces();
                while (enumNetworkInterfaces.hasMoreElements()) {
                    NetworkInterface networkInterface = enumNetworkInterfaces
                            .nextElement();
                    Enumeration<InetAddress> enumInetAddress = networkInterface
                            .getInetAddresses();
                    while (enumInetAddress.hasMoreElements()) {
                        InetAddress inetAddress = enumInetAddress.nextElement();

                        if (inetAddress.isSiteLocalAddress()) {
                            ip += getResources().getString(R.string.startgame_use_this_ip) + " "
                                    + inetAddress.getHostAddress();
                        }

                    }

                }

            } catch (SocketException e) {
                e.printStackTrace();
                ip += "Something Wrong! " + e.toString() + "\n";
            }

            return ip;
        }
    }

    /**
     * start the game
     */
    public void startGame() {
        serverGameController.initiateGame();
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("Host", true);
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        // erase backstack (pressing back-button now leads to home screen)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}