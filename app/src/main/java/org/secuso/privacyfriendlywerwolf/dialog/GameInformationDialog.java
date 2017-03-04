package org.secuso.privacyfriendlywerwolf.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.activity.MainActivity;
import org.secuso.privacyfriendlywerwolf.activity.StartHostActivity;
import org.secuso.privacyfriendlywerwolf.util.Constants;

/**
 * shows the gameinformation in a dialog
 *
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class GameInformationDialog extends DialogFragment {

    StartHostActivity startHostActivity;

    private int amountOfPlayers;
    private float margin = 0;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.getContextOfApplication());

        builder.setTitle(R.string.gameinformation_dialog_title)
                .setIcon(R.drawable.ic_info_outline_black_24dp)
                .setMessage(getResources().getString(R.string.gameinformation_dialog_header)
                        + System.lineSeparator()
                        + System.lineSeparator()
                        + getResources().getString(R.string.gameinformation_dialog_players) + "        " + amountOfPlayers + System.lineSeparator()
                        + getResources().getString(R.string.gameinformation_dialog_werewolves) + "    " + sharedPreferences.getInt(Constants.pref_werewolf_player, 1) + System.lineSeparator()
                        + System.lineSeparator()
                        + getWitchSettingMessage() + System.lineSeparator()
                        + getSeerSettingMessage() + System.lineSeparator()
                        + System.lineSeparator()
                        + getResources().getString(R.string.popup_input_correct)
                        + System.lineSeparator())
                .setPositiveButton(R.string.button_okay, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startHostActivity.startGame();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });


        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().verticalMargin = margin;
        return dialog;
    }

    private String getWitchSettingMessage() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.getContextOfApplication());
        boolean witchPresent = sharedPref.getBoolean(Constants.pref_witch_player, true);
        return witchPresent ? getResources().getString(R.string.gameinformation_dialog_witch_true) : getResources().getString(R.string.gameinformation_dialog_witch_false);
    }

    private String getSeerSettingMessage() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.getContextOfApplication());
        boolean seerPresent = sharedPref.getBoolean(Constants.pref_seer_player, true);
        return seerPresent ? getResources().getString(R.string.gameinformation_dialog_seer_true) : getResources().getString(R.string.gameinformation_dialog_seer_false);
    }

    public void setAmountOfPlayers(int amountOfPlayers) {
        this.amountOfPlayers = amountOfPlayers;
    }

    public void setStartHostActivity(StartHostActivity startHostActivity) {
        this.startHostActivity = startHostActivity;
    }

}
