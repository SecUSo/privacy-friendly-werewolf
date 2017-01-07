package org.secuso.privacyfriendlywerwolf.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.secuso.privacyfriendlywerwolf.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonJoinGame = (Button) findViewById(R.id.buttonJoinGame);
        Button buttonNewGame = (Button) findViewById(R.id.buttonStartNewGame);

        buttonJoinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinGame(view);
            }
        });

        buttonNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewGame(view);
            }
        });

    }

    public void startNewGame(View view){
        Intent intent = new Intent(this, StartHostActivity.class);
        startActivity(intent);
    }

    public void joinGame(View view){
        Intent intent = new Intent(this, StartClientActivity.class);
        startActivity(intent);
    }
}
