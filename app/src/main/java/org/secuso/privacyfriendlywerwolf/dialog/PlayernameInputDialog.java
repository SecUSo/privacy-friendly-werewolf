package org.secuso.privacyfriendlywerwolf.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.activity.MainActivity;
import org.secuso.privacyfriendlywerwolf.activity.StartHostActivity;
import org.secuso.privacyfriendlywerwolf.util.Constants;

import static org.secuso.privacyfriendlywerwolf.util.Constants.pref_playerName;

/**
 * an input dialog which takes the a playerName and starts the lobbyActivity
 *
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class PlayerNameInputDialog extends DialogFragment {

    private EditText userInput;
    private SharedPreferences sharedPref;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        LayoutInflater li = getActivity().getLayoutInflater();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = li.inflate(R.layout.dialog_input_text, null);
        builder.setView(view);
        //getPlayerName from pref, if it was already given
        sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.getContextOfApplication());
        String playerName = sharedPref.getString(pref_playerName, "");
        userInput = (EditText) view.findViewById(R.id.editTextDialogUserInput);
        userInput.setText(playerName);

        // set dialog message
        builder
                .setTitle(R.string.playerNameInput_title)
                .setMessage(R.string.playerNameInput_text)
                .setPositiveButton(R.string.button_okay,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(getActivity(), StartHostActivity.class);
                                intent.putExtra(Constants.PLAYERNAME_PUTEXTRA, userInput.getText().toString());
                                sharedPref.edit().putString(pref_playerName, userInput.getText().toString()).commit();
                                startActivity(intent);
                            }
                        })
                .setNegativeButton(android.R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // go back to main menu if called from the navigation drawer
                                if(getTag().equals("dialog_from_drawer")) {
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    // erase backstack (pressing back-button now leads to home screen)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(intent);
                                }
                            }
                        });


        return builder.create();

    }

}
