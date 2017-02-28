package org.secuso.privacyfriendlywerwolf.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.context.GameContext;
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
            @Override
            public void onClick(View view) {
                startGame();
            }
        });


        ListView list = (ListView) findViewById(R.id.host_player_list);


        stringPlayers = new ArrayList<>();
        fillStringPlayers();

        playerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stringPlayers);
        list.setAdapter(playerAdapter);
        Intent intent = getIntent();
        serverGameController.prepareServerPlayer(intent.getStringExtra(Constants.PLAYERNAME_PUTEXTRA));
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

    /**
     * Game preparation on ui, check if the minimum amount of players has conntected
     * if not print information dialog
     */
    public void startGame() {
        int players = serverGameController.getGameContext().getPlayersList().size();
        if (players >= MIN_PLAYER_COUNT) {
            serverGameController.initiateGame();
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("Host", true);
            startActivity(intent);
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.startgame_need_players)
                    .setMessage(R.string.startgame_need_players_message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // just close and wait for more players
                        }
                    })
                    .setNegativeButton(R.string.button_ignore, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            serverGameController.initiateGame();
                            Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                            intent.putExtra("Host", true);
                            startActivity(intent);
                        }
                    })
                    .setIcon(R.drawable.ic_face_black_24dp)
                    .show();
        }
    }
}