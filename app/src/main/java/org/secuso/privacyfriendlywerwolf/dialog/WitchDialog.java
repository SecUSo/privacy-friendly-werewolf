package org.secuso.privacyfriendlywerwolf.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.activity.GameActivity;
import org.secuso.privacyfriendlywerwolf.client.ClientGameController;

/**
 * Created by Daniel on 13.02.2017.
 */

// TODO: TextDialogWithOptions
public class WitchDialog extends DialogFragment {

    private static final String TAG = "WitchDialog";

    private String dialogTitle;
    private String dialogText;
    private ClientGameController gameController;

    public static WitchDialog newInstance(int elixir) {
        WitchDialog frag = new WitchDialog();
        Bundle args = new Bundle();
        args.putInt("elixir", elixir);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        gameController = ClientGameController.getInstance();
        final int elixir = getArguments().getInt("elixir");

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(dialogTitle)
                .setMessage(dialogText)
                .setPositiveButton(R.string.button_okay, new DialogInterface.OnClickListener() {
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

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if(dialogTitle.equals(getResources().getString(R.string.gamefield_witch_elixir_action))) {
            Log.d(TAG, "OnCancel(): You just cancelled the ELIXIR_Popup without answering, answer again!");
            gameController.getGameActivity().showWitchElixirPopup(dialogTitle, dialogText);
        } else if(dialogTitle.equals(getResources().getString(R.string.gamefield_witch_poison_action))) {
            Log.d(TAG, "OnCancel(): You just cancelled the POISON_Popup without answering, answer again!");
            gameController.getGameActivity().showWitchPoisonPopup(dialogTitle, dialogText);
        } else {
            Log.d(TAG, "OnCancel(): Something went wrong here!");
        }
    }
}
