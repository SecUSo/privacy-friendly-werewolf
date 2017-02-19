package org.secuso.privacyfriendlywerwolf.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.activity.MainActivity;
import org.secuso.privacyfriendlywerwolf.activity.StartHostActivity;
import org.secuso.privacyfriendlywerwolf.util.Constants;

/**
 * an input dialog which takes the a playerName and starts the lobbyActivity
 *
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class PlayerNameInputDialog extends DialogFragment {

    private EditText userInput;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        LayoutInflater li = getActivity().getLayoutInflater();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = li.inflate(R.layout.dialog_input_text, null);
        builder.setView(view);

        userInput = (EditText) view.findViewById(R.id.editTextDialogUserInput);


        // set dialog message
        builder
                .setCancelable(false)
                .setTitle(R.string.playerNameInput_title)
                .setMessage(R.string.playerNameInput_text)
                .setPositiveButton(R.string.button_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(getActivity(), StartHostActivity.class);
                                intent.putExtra(Constants.PLAYERNAME_PUTEXTRA, userInput.getText().toString());
                                startActivity(intent);
                            }
                        })
                .setNegativeButton(R.string.button_decline,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                            }
                        });


        return builder.create();

    }

}
