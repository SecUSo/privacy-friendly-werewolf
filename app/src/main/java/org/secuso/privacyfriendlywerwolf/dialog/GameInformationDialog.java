package org.secuso.privacyfriendlywerwolf.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.activity.MainActivity;
import org.secuso.privacyfriendlywerwolf.activity.StartHostActivity;
import org.secuso.privacyfriendlywerwolf.util.Constants;

/**
 * shows the game information in a dialog
 *
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class GameInformationDialog extends DialogFragment {

    StartHostActivity startHostActivity;

    private int amountOfPlayers;
    private float margin = 0;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.getContextOfApplication());

        builder.setTitle(R.string.gameinformation_dialog_title)
                .setIcon(R.drawable.ic_info_outline_black_24dp)
                // short summary of the settings
                .setMessage(getResources().getString(R.string.gameinformation_dialog_header)
                        + System.lineSeparator()
                        + System.lineSeparator()
                        + getResources().getString(R.string.gameinformation_dialog_players) + "\t\t\t" + amountOfPlayers + System.lineSeparator()
                        + getResources().getString(R.string.gameinformation_dialog_werewolves) + "\t\t\t" + sharedPreferences.getInt(Constants.pref_werewolf_player, 1) + System.lineSeparator()
                        + System.lineSeparator()
                        + getResources().getString(R.string.gameinformation_dialog_witch) + "\t\t\t"
                        + getWitchSettingMessage() + System.lineSeparator()
                        + getResources().getString(R.string.gameinformation_dialog_seer) + "\t\t\t"
                        + getSeerSettingMessage() + System.lineSeparator()
                        + System.lineSeparator()
                        + getResources().getString(R.string.gameinformation_dialog_music) + "\t\t\t"
                        + getBackgroundMusicSettingMessage() + System.lineSeparator()
                        + System.lineSeparator() + System.lineSeparator()
                        + getResources().getString(R.string.popup_input_correct)
                        + System.lineSeparator())
                .setPositiveButton(R.string.button_okay, (dialog, id) -> startHostActivity.startGame())
                .setNegativeButton(android.R.string.no, (dialog, id) -> {
                    // TODO: is this necessary? Just do nothing here.
                    dialog.dismiss();
                });

        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().verticalMargin = margin;
        return dialog;
    }

    // yes if witch was set, else no
    private String getWitchSettingMessage() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.getContextOfApplication());
        boolean witchPresent = sharedPref.getBoolean(Constants.pref_witch_player, true);
        return witchPresent ? getResources().getString(R.string.gameinformation_dialog_witch_true) : getResources().getString(R.string.gameinformation_dialog_witch_false);
    }

    // yes if seer was set, else no
    private String getSeerSettingMessage() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.getContextOfApplication());
        boolean seerPresent = sharedPref.getBoolean(Constants.pref_seer_player, true);
        return seerPresent ? getResources().getString(R.string.gameinformation_dialog_seer_true) : getResources().getString(R.string.gameinformation_dialog_seer_false);
    }

    // yes if background music was set, else no
    private String getBackgroundMusicSettingMessage() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.getContextOfApplication());
        boolean backgroundMusicPresent = sharedPref.getBoolean(Constants.pref_sound_background, true);
        return backgroundMusicPresent ? getResources().getString(R.string.gameinformation_dialog_music_true) : getResources().getString(R.string.gameinformation_dialog_music_false);
    }

    // number of players
    public void setAmountOfPlayers(int amountOfPlayers) {
        this.amountOfPlayers = amountOfPlayers;
    }

    public void setStartHostActivity(StartHostActivity startHostActivity) {
        this.startHostActivity = startHostActivity;
    }
}
