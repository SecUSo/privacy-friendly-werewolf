package org.secuso.privacyfriendlywerwolf.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.controller.GameController;
import org.secuso.privacyfriendlywerwolf.controller.GameControllerImpl;
import org.secuso.privacyfriendlywerwolf.data.PlayerHolder;
import org.secuso.privacyfriendlywerwolf.model.Player;

import java.util.ArrayList;

/**
 * the voting dialog
 *
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class VotingDialog extends DialogFragment {

    //TODO: use custom Player Adapter !!!!
    private ArrayAdapter<String> playerAdapter;
    private ArrayList<String> stringPlayers;
    private GameController gameController;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        fillStringPlayers();
        gameController = GameControllerImpl.getInstance();
        playerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, stringPlayers);

        builder.setTitle(R.string.voting_title)
                .setAdapter(playerAdapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String playerName = stringPlayers.get(which);
                        Player player = PlayerHolder.getInstance().getPlayerByName(playerName);
                        gameController.sendVotingResult(player);
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void fillStringPlayers(){
        stringPlayers = new ArrayList<>();
        for(Player player : PlayerHolder.getInstance().getPlayers()){
            stringPlayers.add(player.getName());
        }
    }

}
