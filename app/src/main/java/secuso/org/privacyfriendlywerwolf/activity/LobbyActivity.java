package secuso.org.privacyfriendlywerwolf.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

import secuso.org.privacyfriendlywerwolf.R;

public class LobbyActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "secuso.org.privacyfriendlywerwolf.MESSAGE";
    private ArrayList<String> players = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
    }

    public void sendName(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_name);
        String name = editText.getText().toString();
        players.add(name);
        //intent.putExtra(EXTRA_MESSAGE, name);
        intent.putStringArrayListExtra(EXTRA_MESSAGE, players);
        startActivity(intent);
    }
}
