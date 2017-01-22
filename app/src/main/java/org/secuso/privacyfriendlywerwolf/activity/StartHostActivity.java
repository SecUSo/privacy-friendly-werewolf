package org.secuso.privacyfriendlywerwolf.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.helpers.PermissionHelper;
import org.secuso.privacyfriendlywerwolf.model.Player;
import org.secuso.privacyfriendlywerwolf.server.ServerGameController;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * StartHostActivity is the default page to start a game host
 * It waits for other clients to connect by creating a new Thread
 *
 * @author Florian Staubach <florian.staubach@stud.tu-darmstadt.de>
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class StartHostActivity extends BaseActivity {

    TextView infoip;
    String message = "";
    Toolbar toolbar;
    ServerGameController serverGameController;
    private static final String TAG = "StartHostActivity";


    //TODO: use custom Player Adapter !!!!
    private ArrayList<Player> players;
    private ArrayList<String> stringPlayers;
    private ArrayAdapter<String> playerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_host);
        infoip = (TextView) findViewById(R.id.infoip);

        infoip.setText(getIpAddress());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle(R.string.startgame_subtitle);

        PermissionHelper.showWifiAlert(this);

        serverGameController = serverGameController.getInstance();
        serverGameController.setStartHostActivity(this);

        serverGameController.startServer();


        Button buttonStart = (Button) findViewById(R.id.btn_start);
        buttonStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                initiateGame();
            }
        });

        ListView list = (ListView) findViewById(R.id.host_player_list);
        players = new ArrayList<>();
        stringPlayers = new ArrayList<>();
        playerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stringPlayers);
        list.setAdapter(playerAdapter);
    }

    @Override
    protected void onDestroy() {
        serverGameController.destroy();
        super.onDestroy();

//        if (serverSocket != null) {
//            try {
                //TODO: use GameController, so he closes the socket. No references here to ServerSocket!
                //serverSocket.close();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
    }

    private void initiateGame(){
        serverGameController.initiateGame();
        //TODO: Go To admin Intent or Game intent
    }

    public void addPlayer(String playerName) {
        Player player = new Player();
        player.setName(playerName);
        players.add(player);
        //TODO: just for now, use @see Player
        stringPlayers.add(playerName);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerAdapter.notifyDataSetChanged();
            }
        });

    }

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
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }
    
}