package secuso.org.privacyfriendlywerwolf.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import secuso.org.privacyfriendlywerwolf.R;

public class GameActivity extends AppCompatActivity {
    ArrayList<String> players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        players = intent.getStringArrayListExtra(LobbyActivity.EXTRA_MESSAGE);

        // Ausgabe Test
        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_game);
        for(int i=0; i<players.size();i++) {
            TextView textView = new TextView(this);
            textView.setTextSize(40);
            textView.setText(players.get(i));


            layout.addView(textView); // Ausgabe Test Ende
        }
    }
}
