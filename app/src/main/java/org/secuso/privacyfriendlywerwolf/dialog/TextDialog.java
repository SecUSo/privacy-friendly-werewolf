package org.secuso.privacyfriendlywerwolf.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import org.secuso.privacyfriendlywerwolf.R;

/**
 * Created by Tobi on 04.02.2017.
 */

public class TextDialog extends DialogFragment {

    //TODO: use custom Player Adapter !!!!
    private String dialogTitle;


    private String dialogText;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        builder.setTitle(dialogTitle)
                .setMessage(dialogText)
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }


    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }

    public void setDialogText(String dialogText) {
        this.dialogText = dialogText;
    }

}
