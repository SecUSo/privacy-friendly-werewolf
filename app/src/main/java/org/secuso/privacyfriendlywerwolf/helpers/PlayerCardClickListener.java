package org.secuso.privacyfriendlywerwolf.helpers;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.client.ClientGameController;
import org.secuso.privacyfriendlywerwolf.context.GameContext;
import org.secuso.privacyfriendlywerwolf.model.Player;

/**
 * Description of the file
 *
 * @author Florian Staubach <florian.staubach@stud.tu-darmstadt.de>
 */

public class PlayerCardClickListener implements View.OnClickListener {

    private static final String TAG = "PlayerCardClickListener";

    ClientGameController clientGameController = ClientGameController.getInstance();
    Player me;
    Player card;

    public PlayerCardClickListener(Player me, Player card) {
        this.me = me;
        this.card = card;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(final View view) {
        Log.d(TAG, "I am an " + me.getPlayerRole());
        Log.d(TAG, "This is the " + GameContext.getInstance().getCurrentPhase() + " Phase!");
        if (me.getPlayerRole() == Player.Role.SEER && GameContext.getInstance().getCurrentPhase() == GameContext.Phase.PHASE_SEER) {
            clientGameController.getGameActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    String message = "The identity is " + card.getPlayerRole().toString();
                    Toast.makeText(clientGameController.getGameActivity(), message, Toast.LENGTH_LONG).show();
                }
            });
        }
        else if (me.getPlayerRole() == Player.Role.WITCH && GameContext.getInstance().getCurrentPhase() == GameContext.Phase.PHASE_WITCH) {
            clientGameController.selectedPlayerForWitch(card);
        }
        // if the clicked card is me, then always show my identity
        else if(me.getPlayerId() == card.getPlayerId()) {
            new AlertDialog.Builder(view.getContext())
                    .setTitle(R.string.gamefield_your_player_card)
                    .setMessage(R.string.gamefield_your_player_card_message)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String message = view.getResources().getString(R.string.gamefield_player_identity);
                            // TODO: is there a nicer way then instanciating the ClientGameContoller here?
                            ClientGameController gameController = ClientGameController.getInstance();
                            message += view.getResources().getString(gameController.getMyPlayer().getPlayerRole().getRole());
                            Toast.makeText(view.getRootView().getContext(), message, Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(R.drawable.ic_face_black_24dp)
                    .show();
        }
        else {
            new AlertDialog.Builder(view.getContext())
                    .setTitle(R.string.gamefield_player_card)
                    .setMessage(R.string.gamefield_player_card_message)
                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(R.drawable.ic_face_black_24dp)
                    .show();
        }

    }
}
