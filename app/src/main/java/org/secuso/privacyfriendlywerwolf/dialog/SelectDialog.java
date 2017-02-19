package org.secuso.privacyfriendlywerwolf.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.client.ClientGameController;
import org.secuso.privacyfriendlywerwolf.context.GameContext;
import org.secuso.privacyfriendlywerwolf.model.Player;

import java.util.ArrayList;

/**
 * the voting dialog
 *
 * @author Florian Staubach <florian.staubach@stud.tu-darmstadt.de>
 */
public class SelectDialog extends DialogFragment {

    //TODO: use custom Player Adapter !!!!
    private ArrayAdapter<String> playerAdapter;
    private ArrayList<String> stringPlayers;
    private ClientGameController gameController;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        fillStringPlayers();
        gameController = ClientGameController.getInstance();
        playerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, stringPlayers);

        builder.setTitle(R.string.voting_title)
                .setAdapter(playerAdapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String playerName = stringPlayers.get(which);
                        Player player = GameContext.getInstance().getPlayerByName(playerName);

                        gameController.getGameActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });

                    }
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

}