package org.secuso.privacyfriendlywerwolf.controller;

import android.util.Log;

import org.secuso.privacyfriendlywerwolf.activity.GameActivity;
import org.secuso.privacyfriendlywerwolf.activity.StartClientActivity;
import org.secuso.privacyfriendlywerwolf.client.WebsocketClientHandler;
import org.secuso.privacyfriendlywerwolf.context.GameContext;
import org.secuso.privacyfriendlywerwolf.model.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * updates the model on the client, aswell as the view on the client and initiates communication to the server
 *
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 * @author Florian Staubach <florian.staubach@stud.tu-darmstadt.de>
 */
public class GameControllerImpl extends Controller implements GameController{

    private static final String TAG = "GameControllerImpl";
    private static final GameControllerImpl GAME_CONTROLLER = new GameControllerImpl();

    StartClientActivity startClientActivity;
    GameActivity gameActivity;
    WebsocketClientHandler websocketClientHandler;
    GameContext gameContext;

    private GameControllerImpl() {
        Log.d(TAG, "GameController singleton created");
        websocketClientHandler = new WebsocketClientHandler();
        websocketClientHandler.setGameController(this);
        gameContext = GameContext.getInstance();
    }

    public static GameController getInstance() {
        return GAME_CONTROLLER;

    }

    public void startGame(GameContext gc) {
        //TODO: extract the roles of the players and give it to the activity
        //TODO: extract every other information which were send by the server

        gameContext.copy(gc);
        startClientActivity.startGame();
    }

    /*
    public void initiateWerewolfPhase() {
        // TODO: Strings nicht hardcoden
        gameActivity.outputMessage("Die Werwölfe erwachen und suchen sich ein Opfer!");
        gameActivity.openVoting();
        gameActivity.outputMessage("Die Werwölfe haben sich jemanden ausgesucht, super!");
        // TODO: only needed if GameMaster (GM) plays as well
        // go to the next state automatically (without GM interference)
        websocketClientHandler.send("nextPhase");
        gameActivity.outputMessage("Die Werwölfe schlafen nun wieder ein");
    }*/

    public void initiateWerewolfPhase() {
        gameActivity.outputMessage("Die Werwölfe erwachen und suchen sich ein Opfer!");
        //voting("Werewolf");
        websocketClientHandler.send("nextPhase");
    }

    public void endWerewolfPhase() {
        gameActivity.outputMessage("Die Werwölfe haben ihr Opfer gefunden und schlafen wieder ein!");
        // TODO: only needed if GameMaster (GM) plays as well
        // go to the next state automatically (without GM interference)
        //websocketClientHandler.send("nextPhase");
    }

    public void initiateVotingPhase() {
        startVoting();
    }

    public void initiateWitchPhase() {
        // TODO: wenn die Hexe tot ist
        gameActivity.outputMessage("Die Hexe erwacht!");
        gameActivity.outputMessage("Die Hexe entscheidet ob sie Tränke einsetzen möchte");
        useElixirs();
        gameActivity.outputMessage("Die Hexe hat ihre Entscheidung getroffen!");

        // TODO: only needed if GameMaster (GM) plays as well
        // go to the next state automatically (without GM interference)
        //websocketClientHandler.send("nextPhase");
        gameActivity.outputMessage("Die Hexe schläft nun wieder ein");
    }

    public void initiateSeerPhase() {
        // TODO: wenn die Hexe tot ist
        gameActivity.outputMessage("Die Seherin erwacht!");
        gameActivity.outputMessage("Die Seherin wählt einen Spieler aus, dessen Karte sie sich ansehen möchte");
        useSeerPower();
        gameActivity.outputMessage("Die Seherin kennt jetzt ein Geheimnis mehr!");

        // TODO: only needed if GameMaster (GM) plays as well
        // go to the next state automatically (without GM interference)
        //websocketClientHandler.send("nextPhase");
        gameActivity.outputMessage("Die Seherin schläft nun wieder ein");
    }

    public void initiateDayPhase() {
        // TODO: wenn die Hexe tot ist
        gameActivity.outputMessage("Es wird hell und alle Dorfbewohner erwachen aus ihrem tiefen Schlaf");
        gameActivity.outputMessage("Leider von uns gegangen sind...");
        /*String[] deceasedPlayers = new String[gameContext.getNumberOfCasualties()];
        fillDeathList(deceasedPlayers);
        for(int i=0;i<deceasedPlayers.length;i++) {
            gameActivity.outputMessage(deceasedPlayers[i]);
        }*/
        gameActivity.outputMessage("Die übrigen Bewohner können jetzt abstimmen.");
        websocketClientHandler.send("nextPhase");

    }

    public void endDayPhase() {
        gameActivity.outputMessage("Die Abstimmung ist beendet...");
        // TODO: Detaillierte Sprachausgabe: solche Details auch in die Ausgabe? (finde ich zu viel)
        gameActivity.outputMessage("Hans wurde ausgewählt, und er ist...ein Horst!");


        // TODO: only needed if GameMaster (GM) plays as well
        // go to the next state automatically (without GM interference)
        //websocketClientHandler.send("nextPhase");
        gameActivity.outputMessage("Alle schlafen wieder ein, es wird Nacht!");
    }


    public void fillDeathList(String[] deceasedPlayers) {
        // TODO: Hunger Games Tribute Death Theme abspielen!!
        deceasedPlayers[0] = "Tobias :(";
        deceasedPlayers[1] = "Flo :(";
        deceasedPlayers[2] = "Klaus :(";
        // TODO: get the deceased players from the GameContext diff between last round and currentRound
    }



    public void voting(String role) {
        // Werwolf voting (only werewolves vote)
        // Dorfbewohner voting (every living role votes)
    }

    public void useElixirs() {
        Log.i(TAG, "Hexe setzt ihre Fähigkeit ein");
        gameActivity.showElixirs();
        // TODO: implement Witch logic
    }

    public void useSeerPower() {
        Log.i(TAG, "Seherin setzt ihre Fähigkeit ein");
        // TODO: implement Seer logic
    }

    private List<Player> extractPlayers(String playerString){
        ArrayList<Player> players = new ArrayList<>();
        String cuttedPlayers = playerString.replace("startGame_", " ").trim();

        String[] playerArray = cuttedPlayers.split("&");
        for(String playerNameString : playerArray){
            Player p = new Player();
            p.setName(playerNameString);
            players.add(p);
        }

        return players;
    }

    @Override
    public void startVoting() {
        gameActivity.openVoting();
    }


    @Override
    public void sendVotingResult(Player player) {
        websocketClientHandler.send("votingResult_"+player.getPlayerName());
    }

    @Override
    public void handleVotingResult(String playerName) {

        Log.d(TAG,"voting_result received. Kill this guy: "+ playerName);
        Player playerToKill = GameContext.getInstance().getPlayerByName(playerName);
        playerToKill.setDead(true);
        gameActivity.renderButtons();
        websocketClientHandler.send("nextPhase");
    }


    public void connect(String url, String playerName){
        websocketClientHandler.startClient(url, playerName);
    }

    public GameActivity getGameActivity() {
        return gameActivity;
    }

    public void setGameActivity(GameActivity gameActivity) {
        this.gameActivity = gameActivity;
    }

    public StartClientActivity getStartClientActivity() {
        return startClientActivity;
    }

    public void setStartClientActivity(StartClientActivity startClientActivity) {
        this.startClientActivity = startClientActivity;
    }

    public WebsocketClientHandler getWebsocketClientHandler() {
        return websocketClientHandler;
    }

    public void setWebsocketClientHandler(WebsocketClientHandler websocketClientHandler) {
        this.websocketClientHandler = websocketClientHandler;
    }
}
