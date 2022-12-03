package org.secuso.privacyfriendlywerwolf.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import org.secuso.privacyfriendlywerwolf.R;

/**
 * a generic text dialog, which shows a title and a message
 *
 * use the setters. constructor passing is unwanted by android.
 *
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class TextDialog extends DialogFragment {
    private String dialogTitle;
    private String dialogText;
    private float margin = 0;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        builder.setTitle(dialogTitle)
                .setMessage(dialogText)
                .setPositiveButton(R.string.button_okay, (dialog, id) -> {

                });

        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().verticalMargin = margin;
        return dialog;
    }


    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }

    public void setDialogText(String dialogText) {
        this.dialogText = dialogText;
    }

    public void setMargin(float margin) {
        this.margin = margin;
    }
}
