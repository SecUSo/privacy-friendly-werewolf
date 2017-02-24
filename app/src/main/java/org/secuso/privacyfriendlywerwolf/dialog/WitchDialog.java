package org.secuso.privacyfriendlywerwolf.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.activity.GameActivity;
import org.secuso.privacyfriendlywerwolf.client.ClientGameController;

/**
 * Created by Daniel on 13.02.2017.
 */

// TODO: TextDialogWithOptions (allgemeiner TextDialog mit ja/nein Option)
public class WitchDialog extends DialogFragment {

    // TODO: in Bundle stecken
    private String dialogTitle;
    private String dialogText;

    public static WitchDialog newInstance(int elixir) {
        WitchDialog frag = new WitchDialog();
        Bundle args = new Bundle();
        args.putInt("elixir", elixir);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final int elixir = getArguments().getInt("elixir");

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(dialogTitle)
                .setMessage(dialogText)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((GameActivity) getActivity()).doPositiveClick(elixir);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((GameActivity) getActivity()).doNegativeClick(elixir);
                    }
                })
                .setIcon(R.drawable.ic_local_drink_black_24dp)
                .create();

        dialog.getWindow().getAttributes().verticalMargin = -0.1F;
        return dialog;
    }



    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }

    public void setDialogText(String dialogText) {
        this.dialogText = dialogText;
    }

}
