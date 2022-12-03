package org.secuso.privacyfriendlywerwolf.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.activity.GameActivity;
import org.secuso.privacyfriendlywerwolf.client.ClientGameController;

/**
 * Created by Daniel on 13.02.2017.
 */
public class WitchDialog extends DialogFragment {
    private static final String TAG = "WitchDialog";

    private String dialogTitle;
    private String dialogText;
    private ClientGameController gameController;

    public static WitchDialog newInstance(int elixir) {
        WitchDialog frag = new WitchDialog();
        Bundle args = new Bundle();
        // pass in the elixir type
        args.putInt("elixir", elixir);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        gameController = ClientGameController.getInstance();
        final int elixir = getArguments().getInt("elixir");

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(dialogTitle)
                .setMessage(dialogText)
                .setPositiveButton(R.string.button_okay, (dialog12, which) -> {
                    // use the elixir
                    ((GameActivity) getActivity()).doPositiveClick(elixir);
                })
                // do not use the elixir
                .setNegativeButton(android.R.string.no, (dialog1, which) -> ((GameActivity) getActivity()).doNegativeClick(elixir))
                .setIcon(R.drawable.ic_local_drink_black_24dp)
                .create();

        // slightly move the dialog window up, to avoid overlap with previous popup
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
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        // if somehow cancelld without answering, reopen the dialog
        if (dialogTitle.equals(getResources().getString(R.string.gamefield_witch_elixir_action))) {
            Log.d(TAG, "OnCancel(): You just cancelled the ELIXIR_Popup without answering, answer again!");
            gameController.getGameActivity().showWitchElixirPopup();
        } else if (dialogTitle.equals(getResources().getString(R.string.gamefield_witch_poison_action))) {
            Log.d(TAG, "OnCancel(): You just cancelled the POISON_Popup without answering, answer again!");
            gameController.getGameActivity().showWitchPoisonPopup();
        } else {
            Log.d(TAG, "OnCancel(): Something went wrong here!");
        }
    }
}
