package org.secuso.privacyfriendlywerwolf.helpers;

import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.client.ClientGameController;
import org.secuso.privacyfriendlywerwolf.context.GameContext;
import org.secuso.privacyfriendlywerwolf.enums.GamePhaseEnum;
import org.secuso.privacyfriendlywerwolf.model.Player;

/**
 * Click listener for the player cards
 *
 * @author Florian Staubach <florian.staubach@stud.tu-darmstadt.de>
 */

public class PlayerCardClickListener implements View.OnClickListener {

    private static final String TAG = "PlayerCardClickListener";

    ClientGameController clientGameController = ClientGameController.getInstance();
    Player me;          // the player who clicked the cardr
    Player card;        // the card clicked on

    public PlayerCardClickListener(Player me, Player card) {
        this.me = me;
        this.card = card;
    }

    /**
     * Called when a player card has been clicked.
     *
     * @param view The card that was clicked.
     */
    @Override
    public void onClick(final View view) {
        Log.d(TAG, "I am an " + me.getPlayerRole());
        Log.d(TAG, "This is the " + GameContext.getInstance().getCurrentPhase() + " Phase!");

        // the seer clicked
        if (me.getPlayerRole() == Player.Role.SEER && GameContext.getInstance().getCurrentPhase() == GamePhaseEnum.PHASE_SEER) {
            // if clicked player is dead
            if (card.isDead()
                    // and the selected player not killed in this night
                    && (
                    // looking at the person that got killed by the Werewolves:
                    // if the Werwolves killed no one this round (cant really happen, but for consistency)
                    ((clientGameController.getPlayerKilledByWerewolvesName() == null) ||
                    // if the Werwolves killed a person this round
                    (clientGameController.getPlayerKilledByWerewolvesName() != null
                            // if this person is not the clicked player..
                            && (!clientGameController.getPlayerKilledByWerewolvesName().getPlayerName().equals(card.getPlayerName()))))
                            && // and
                            // looking at the person that got killed by the Witch:
                            // if the Witch killed no one this round (this can happen)
                            ((clientGameController.getPlayerKilledByWitchName() == null) ||
                            // if the Witch killed a person this round
                            (clientGameController.getPlayerKilledByWitchName() != null
                                    // if this person is not the clicked player..
                                    && (!clientGameController.getPlayerKilledByWitchName().getPlayerName().equals(card.getPlayerName())))))) {
                // Tell the seer that the clicked has long passed (isDead), and that she should pick another person
                clientGameController.getGameActivity().showTextPopup(R.string.popup_title_choose_another, R.string.popup_text_choose_another);
            } else { // everything is fine, tell seer the identity
                String message = clientGameController.getGameActivity().getResources().getString(R.string.common_identity_of)
                        + " " + card.getPlayerName()  + " " + clientGameController.getGameActivity().getResources().getString(R.string.common_is)
                        + " " + view.getResources().getString(card.getPlayerRole().getRole());
                clientGameController.getGameActivity().showTextPopup(R.string.popup_title_seer_power, message);
                clientGameController.getGameActivity().runOnGameThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "SeerEnd - Current Thread: " + Thread.currentThread().getName());
                        clientGameController.sendDoneToServer();
                    }
                }, 2000);
            }

            // the witch clicked
        } else if (me.getPlayerRole() == Player.Role.WITCH && GameContext.getInstance().getCurrentPhase() == GamePhaseEnum.PHASE_WITCH_POISON) {
            if (!card.isDead()) {
                clientGameController.getGameActivity().runOnGameThread(new Runnable() {
                    @Override
                    public void run() {
                        // mark clicked player as poisoned
                        clientGameController.selectedPlayerForWitch(card);
                    }
                }, 0);
            } else {
                // cannot poison dead players
                clientGameController.getGameActivity().showTextPopup(R.string.popup_title_poison_another, R.string.popup_text_poison_another);
            }
        }
        // if the clicked card is me, then always show my identity
        else if (me.getPlayerId() == card.getPlayerId())

        {
            if (!card.isDead()) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle(R.string.gamefield_your_player_card)
                        .setMessage(R.string.gamefield_your_player_card_message)
                        .setPositiveButton(R.string.button_okay, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String message = view.getResources().getString(R.string.gamefield_player_identity);
                                message += view.getResources().getString(clientGameController.getMyPlayer().getPlayerRole().getRole());
                                Toast.makeText(view.getRootView().getContext(), message, Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(R.drawable.ic_face_black_24dp)
                        .setCancelable(false)
                        .show();
            } else {
                clientGameController.getGameActivity().showTextPopup(R.string.popup_title_dead, R.string.popup_text_dead,
                        clientGameController.getGameActivity().getResources().getString(card.getPlayerRole().getRole()));
            }
        } else

        {   // another player's card
            if (!card.isDead()) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle(R.string.gamefield_player_card)
                        .setMessage(R.string.gamefield_player_card_message)
                        .setPositiveButton(R.string.button_okay, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(R.drawable.ic_face_black_24dp)
                        .show();
            } else {
                clientGameController.getGameActivity().showTextPopup(R.string.popup_title_dead_2, R.string.popup_text_dead_2,
                        clientGameController.getGameActivity().getResources().getString(card.getPlayerRole().getRole()));
            }
        }

    }
}
