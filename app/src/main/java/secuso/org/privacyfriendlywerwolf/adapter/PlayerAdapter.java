package secuso.org.privacyfriendlywerwolf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import secuso.org.privacyfriendlywerwolf.R;
import secuso.org.privacyfriendlywerwolf.model.Player;

/**
 * Created by Tobi on 06.12.2016.
 */

public class PlayerAdapter extends ArrayAdapter<Player> {

    public PlayerAdapter(Context context, ArrayList<Player> players) {
        super(context, 0, players);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Player player = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            //convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }
        // Lookup view for data population
        //TextView playername = (TextView) convertView.findViewById(R.id.player);
        // Populate the data into the template view using the data object
       // playername.setText(player.getName());

        // Return the completed view to render on screen
        return convertView;
    }
}
