package org.secuso.privacyfriendlywerwolf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.context.GameContext;
import org.secuso.privacyfriendlywerwolf.helpers.PlayerCardClickListener;
import org.secuso.privacyfriendlywerwolf.model.Player;

import java.util.List;

/**
 * Adapter to render the buttons on the game field
 * so that there is a strong connection between card and player
 *
 * @author Florian Staubach <florian.staubach@stud.tu-darmstadt.de>
 */
public class PlayerAdapter extends BaseAdapter {

    private final Context context;
    private final List<Player> players;
    private final long myId;

    /**
     * Constructor
     *
     * @param context The current context.
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
            TextView textView = gridView
                    .findViewById(R.id.player_item_label);
            textView.setText(player.getPlayerName());

            ImageButton playerButton = gridView
                    .findViewById(R.id.player_item_button);
          

            // change card image depending on the current player status
            if(player.isDead()) {
                playerButton.setBackgroundResource(R.mipmap.player_button_dead);
                playerButton.invalidate();
            } else if (player.getPlayerId() == myId){
                playerButton.setBackgroundResource(R.mipmap.player_button_me);

            } else {
                playerButton.setBackgroundResource(R.mipmap.player_button);
            }
            // set click listener for all player buttons
            playerButton.setOnClickListener(new PlayerCardClickListener(GameContext.getInstance().getPlayerById(myId), player));
        } else {
            gridView = convertView;
        }

        return gridView;

    }
}
