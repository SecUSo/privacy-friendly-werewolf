package org.secuso.privacyfriendlywerwolf.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.client.ClientGameController;
import org.secuso.privacyfriendlywerwolf.context.GameContext;
import org.secuso.privacyfriendlywerwolf.model.Player;

import java.util.ArrayList;

/**
 * the voting dialog
 *
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class VotingDialog extends DialogFragment {
    private static final String TAG = "VotingDialog";

    //TODO: use custom Player Adapter
    private ArrayAdapter<String> playerAdapter;
    private ArrayList<String> stringPlayers;
    private ClientGameController gameController;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        fillStringPlayers();
        gameController = ClientGameController.getInstance();
        playerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, stringPlayers);

        builder.setTitle(R.string.voting_title)
                .setAdapter(playerAdapter, (dialog, which) -> {
                    //TODO: use a correct playerAdapter to get by id
                    String playerName = stringPlayers.get(which);
                    final Player player = GameContext.getInstance().getPlayerByName(playerName);

                    gameController.getGameActivity().runOnGameThread(() -> gameController.sendVotingResult(player), 0);
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void fillStringPlayers(){
        stringPlayers = new ArrayList<>();
        for(Player player : GameContext.getInstance().getPlayersList()){
            if(!player.isDead()){
                stringPlayers.add(player.getPlayerName());
            }
        }
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        // if somehow cancelled without voting, reopen dialog
        Log.d(TAG, "OnCancel(): You just cancelled the VOTING_Popup without voting, vote again!");
        gameController.getGameActivity().openVoting();
    }
}
