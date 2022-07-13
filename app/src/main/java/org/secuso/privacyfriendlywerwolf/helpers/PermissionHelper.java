package org.secuso.privacyfriendlywerwolf.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.activity.MainActivity;

/**
 * PermissionHelper offers methods to check permissions and if they are not granted it will
 * show a notification to the user to grant the permissions
 *
 * @author Florian Staubach <florian.staubach@stud.tu-darmstadt.de>
 */

public class PermissionHelper {
    private static final int PERMISSIONS_REQUEST_INTERNET = 0;
    private static final String TAG = "PermissionHelper";
    private static WifiManager.LocalOnlyHotspotReservation hotspotReservation;

    public static boolean isWifiEnabled(final Context context) {
        return getWifiManager(context).isWifiEnabled();
    }

    public static @Nullable String getHotspotSSID() {
        if (hotspotReservation == null) return null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return hotspotReservation.getSoftApConfiguration().getSsid();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return hotspotReservation.getWifiConfiguration().SSID;
        } else return null;
    }

    public static String getHotspotPassphrase() {
        if (hotspotReservation == null) return null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return hotspotReservation.getSoftApConfiguration().getPassphrase();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return hotspotReservation.getWifiConfiguration().preSharedKey;
        } else return null;
    }

    public static void showWifiAlert(final Activity context) {
        if (!getWifiManager(context).isWifiEnabled()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setTitle(R.string.startgame_need_wifi)
                    .setIcon(R.drawable.ic_signal_wifi_off_black_24dp)
                    .setCancelable(false);

            // this is a button for testing with emulators. because emulators always
            // return false on method isWifiEnabled, when pressing "OKAY" emulator handy
            // always gets redirected to the main menu, so we have a emulator button here
//            builder.setNeutralButton(R.string.emulator, (DialogInterface.OnClickListener) (dialog, which) -> {
//                // just a button for testing. keeps emulator in clientActivity
//            })

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (hotspotReservation != null) return;
                builder.setMessage(R.string.startgame_need_wifi_message_hotspot);
                builder.setNegativeButton(android.R.string.no, (dialog, which) -> returnToMenu(context));
                builder.setPositiveButton(R.string.startgame_create_hotspot, (dialog, which) -> setupHotspot(context));
            } else {
                builder.setMessage(R.string.startgame_need_wifi_message);
                builder.setPositiveButton(R.string.button_okay, (dialog, which) -> returnToMenu(context));
            }
            builder.show();
        }
    }

    private static void setupHotspot(final Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_INTERNET);
                return;
            }

            if (!getLocationManager(context).isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.startgame_create_hotspot_failed)
                        .setIcon(R.drawable.ic_signal_wifi_off_black_24dp)
                        .setCancelable(false)
                        .setMessage(R.string.startgame_create_hotspot_failed_gps_message)
                        .setPositiveButton(R.string.button_okay, (dialog, which) -> returnToMenu(context))
                        .show();
                return;
            }

            getWifiManager(context).startLocalOnlyHotspot(new WifiManager.LocalOnlyHotspotCallback() {
                @Override
                public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
                    super.onStarted(reservation);
                    hotspotReservation = reservation;
                    showWifiAlert(context);
                    context.recreate();
                }

                @Override
                public void onStopped() {
                    super.onStopped();
                    hotspotReservation = null;
                }

                @Override
                public void onFailed(int reason) {
                    super.onFailed(reason);
                    Log.d(TAG, "Could not set up hotspot: " + reason);
                    new AlertDialog.Builder(context)
                            .setTitle(R.string.startgame_create_hotspot_failed)
                            .setIcon(R.drawable.ic_signal_wifi_off_black_24dp)
                            .setCancelable(false)
                            .setMessage(R.string.startgame_create_hotspot_failed_message)
                            .setPositiveButton(R.string.button_okay, (dialog, which) -> returnToMenu(context))
                            .show();
                }
            }, new Handler());
        }
    }

    public static void handlePermissionRequestResult(AppCompatActivity context, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_INTERNET:
                if (permissions.length == 2
                        && permissions[0].equals(Manifest.permission.ACCESS_COARSE_LOCATION)
                        && permissions[1].equals(Manifest.permission.ACCESS_FINE_LOCATION)
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupHotspot(context);
                } else {
                    Log.d(TAG, "User did not allow location access, which is required for the hotspot");
                    returnToMenu(context);
                }
                break;
            default:
                Log.e(TAG, "Attempted to handle permission request result with unknown code: " + requestCode);
                returnToMenu(context);
                break;
        }
    }

    private static WifiManager getWifiManager(Context context) {
        return (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    private static LocationManager getLocationManager(Context context) {
        return (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
    }

    private static void returnToMenu(Activity context) {
        // go back to main menu
        Intent intent = new Intent(context, MainActivity.class);
        // erase backstack (pressing back-button now leads to home screen)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }
}
