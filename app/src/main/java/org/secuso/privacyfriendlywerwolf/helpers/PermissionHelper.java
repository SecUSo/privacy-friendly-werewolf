package org.secuso.privacyfriendlywerwolf.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.activity.MainActivity;

import java.util.List;

/**
 * PermissionHelper offers methods to check permissions and if they are not granted it will
 * show a notification to the user to grant the permissions
 *
 * @author Florian Staubach <florian.staubach@stud.tu-darmstadt.de>
 */

public class PermissionHelper {

    private static final int PERMISSIONS_REQUEST_INTERNET = 0;

    public static boolean isWifiEnabled(final Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    public static void showWifiAlert(final Context context) {

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        // check if we are allowed to check permissions
        // TODO: what is this for?
        /*if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED || !wifiManager.isWifiEnabled()) {*/

            // we show an permission request explanation if wifi is turned of or no permissions
            // TODO: what is this for?
            /*if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    Manifest.permission.INTERNET) ||*/
            if(!wifiManager.isWifiEnabled()) {

                new AlertDialog.Builder(context)
                        .setTitle(R.string.startgame_need_wifi)
                        .setMessage(R.string.startgame_need_wifi_message)
                        // TODO: this brings trouble on my phone/ makes things complicated
                        /*.setNegativeButton(R.string.startgame_need_wifi_open_settings, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Intent intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS, null);
                                List<ResolveInfo> activities = context.getPackageManager().queryIntentActivities(intent, 0);

                                // if on the device is now wifi settings available use standard wireless settings
                                // if no settings at all, then just don't do anything
                                if(activities.size() == 0) {
                                    intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                                    activities = context.getPackageManager().queryIntentActivities(intent, 0);

                                    if(activities.size() != 0) {
                                        context.startActivity(intent);
                                    }
                                }

                            }
                        })*/

                        // this is a button for testing with emulators. because emulators always
                        // return false on method isWifiEnabled, when pressing "OKAY" emulator handy
                        // always gets redirected to the main menu, so we have a emulator button here
                        /*.setNeutralButton(R.string.emulator, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // just a button for testing. keeps emulator in clientActivity
                            }
                        })*/
                        .setPositiveButton(R.string.button_okay, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // go back to main menu
                                Intent intent = new Intent(context, MainActivity.class);
                                // erase backstack (pressing back-button now leads to home screen)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                context.startActivity(intent);
                            }
                        })
                        .setIcon(R.drawable.ic_signal_wifi_off_black_24dp)
                        .setCancelable(false)
                        .show();
            }

        /*} else {

            // No explanation needed, we can request the permission.
            // TODO: why?
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_INTERNET);
        }*/
    }
}
