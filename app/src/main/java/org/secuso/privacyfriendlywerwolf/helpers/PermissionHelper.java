package org.secuso.privacyfriendlywerwolf.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import org.secuso.privacyfriendlywerwolf.R;

/**
 * PermissionHelper offers methods to check permissions and if they are not granted it will
 * show a notification to the user to grant the permissions
 *
 * @author Florian Staubach <florian.staubach@stud.tu-darmstadt.de>
 */

public class PermissionHelper {

    private static final int PERMISSIONS_REQUEST_INTERNET = 0;


    public static void showWifiAlert(Context context) {

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if(wifiManager.isWifiEnabled()) {

        }

        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED || !wifiManager.isWifiEnabled()) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    Manifest.permission.INTERNET) || !wifiManager.isWifiEnabled()) {

                new AlertDialog.Builder(context)
                        .setTitle(R.string.startgame_need_wifi)
                        .setMessage(R.string.startgame_need_wifi_message)
                        .setPositiveButton(R.string.startgame_need_wifi_open_settings, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO: implement intent to wifi settings
                            }
                        })
                        .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // close
                            }
                        })
                        .setIcon(R.drawable.ic_signal_wifi_off_black_24dp)
                        .show();
            }

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        PERMISSIONS_REQUEST_INTERNET);

            }
        }


}
