package org.secuso.privacyfriendlywerwolf.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.client.ClientGameController;
import org.secuso.privacyfriendlywerwolf.context.GameContext;
import org.secuso.privacyfriendlywerwolf.model.Player;

import java.util.List;

/**
 * Adapter to render the buttons on the game field
 * so that there is a strong connection between card and player
 *
 * @author Florian Staubach <florian.staubach@stud.tu-darmstadt.de>
 */
public class PlayerAdapter extends BaseAdapter {

    private Context context;
    private List<Player> players;
    private long myId;

    /**
     * Constructor
     *
     * @param context The current context.
     * @param players The player objects to represent in the GridView.
     * @param myId The own player id to recognize which card is one's own card
     */
    public PlayerAdapter(Context context, long myId) {
        this.context = context;
        this.players = GameContext.getInstance().getPlayersList();
        this.myId = myId;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return players.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return players.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        // if there was no view created yet, do it
        if(convertView == null) {

            // get the GridView from the GameField
            gridView = inflater.inflate(R.layout.player_item, null);

            Player player = players.get(position);

            // set the label and button for the current player
            TextView textView = (TextView) gridView
                    .findViewById(R.id.player_item_label);
            textView.setText(player.getPlayerName());

            ImageButton playerButton = (ImageButton) gridView
                    .findViewById(R.id.player_item_button);

            // change card image depending on the current player status
            if(player.isDead()) {
                playerButton.setBackgroundResource(R.mipmap.player_button_dead);
                playerButton.invalidate();
            } else if (player.getPlayerId() == myId){
                playerButton.setBackgroundResource(R.mipmap.player_button_me);
                playerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(view.getContext())
                                .setTitle(R.string.gamefield_your_player_card)
                                .setMessage(R.string.gamefield_your_player_card_message)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        String message = context.getResources().getString(R.string.gamefield_player_identity);
                                        // TODO: is there a nicer way then instanciating the ClientGameContoller here?
                                        ClientGameController gameController = ClientGameController.getInstance();
                                        message += context.getResources().getString(gameController.getMyPlayer().getPlayerRole().getRole());
                                        Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_LONG).show();
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
                });
            } else {
                playerButton.setBackgroundResource(R.mipmap.player_button);
                playerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
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
                });
            }
        } else {
            gridView = (View) convertView;
        }

        return gridView;

    }
}
