package org.secuso.privacyfriendlywerwolf.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.context.GameContext;
import org.secuso.privacyfriendlywerwolf.controller.GameController;
import org.secuso.privacyfriendlywerwolf.controller.GameControllerImpl;
import org.secuso.privacyfriendlywerwolf.model.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Game activity is the game field to render the game on the screen
 *
 *
 * @author Florian Staubach <florian.staubach@stud.tu-darmstadt.de>
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public class GameActivity extends BaseActivity {

    List<Player> players;
    List<Button> playerButtons;

    // this is important
    GameController gameController;

    /**
     * Let's start a new activity to start the game
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        playerButtons = new ArrayList<>();
        gameController = GameControllerImpl.getInstance();
        gameController.setGameActivity(this);

        players =  GameContext.getInstance().getPlayersList();

        gameController = GameControllerImpl.getInstance();
        gameController.setGameActivity(this);

        // Ausgabe Test
        GridLayout layout = (GridLayout) findViewById(R.id.players);
        Button example_button = (Button) findViewById(R.id.example_button);
        ViewGroup.LayoutParams button_layout = example_button.getLayoutParams();
        layout.removeView(example_button);

        //TODO: DANIEL: use playeradapter instead of this shit
        for (int i = 0; i < players.size(); i++) {

            Button button = new Button(this);
            button.setText(players.get(i).getPlayerName());
            button.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
            button.setMinimumHeight(340);
            button.setMinimumWidth(340);
            button.setBackgroundResource(R.mipmap.app_icon);

            button.setOnClickListener(new View.OnClickListener() {
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

            layout.addView(button);
            playerButtons.add(button);

        } // Ausgabe Test Ende
    }

    public void openVoting() {
        VotingDialog votingDialog = new VotingDialog();
        votingDialog.show(getFragmentManager(), "voting");

    }

    public void renderButtons() {
       //TODO: render buttons, and new icons
        for(Button playerButton : playerButtons){
           Player player = GameContext.getInstance().getPlayerByName(playerButton.getText().toString());
           if(player.isDead()){
               runOnUiThread(new Runnable() {
                   Button playerButton;

                   private Runnable init(Button button) {
                       playerButton = button;
                       return this;
                   }
                   @Override
                   public void run() {
                       //TODO: set a new icon!
                       playerButton.setText("TOT !!!");
                       playerButton.invalidate();
                   }
               }.init(playerButton));


           }
       }
    }

    public void outputMessage(final String message) {
        // accessing UI thread from background thread
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void showElixirs() {
        // TODO: make the healing potion and the poisoned potion visible (use buttons)
        // make buttons gray depending if already used or not, also use output message
        // depending on potion usage
    }



}
