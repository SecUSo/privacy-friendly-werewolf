package org.secuso.privacyfriendlywerwolf.client;

import android.util.Log;

import org.secuso.privacyfriendlywerwolf.R;
import org.secuso.privacyfriendlywerwolf.activity.GameActivity;
import org.secuso.privacyfriendlywerwolf.activity.StartClientActivity;
import org.secuso.privacyfriendlywerwolf.context.GameContext;
import org.secuso.privacyfriendlywerwolf.controller.Controller;
import org.secuso.privacyfriendlywerwolf.model.NetworkPackage;
import org.secuso.privacyfriendlywerwolf.model.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * updates the model on the client, aswell as the view on the client and initiates communication to the server
 *
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 * @author Florian Staubach <florian.staubach@stud.tu-darmstadt.de>
 */
public class ClientGameController extends Controller {

    private static final String TAG = "ClientGameController";
    private static final ClientGameController GAME_CONTROLLER = new ClientGameController();

    Player me;
    long myId;

    StartClientActivity startClientActivity;
    GameActivity gameActivity;
    WebsocketClientHandler websocketClientHandler;
    GameContext gameContext;

    private ClientGameController() {
        Log.d(TAG, "GameController singleton created");
        websocketClientHandler = new WebsocketClientHandler();
        websocketClientHandler.setGameController(this);
        gameContext = GameContext.getInstance();
    }

    public static ClientGameController getInstance() {
        return GAME_CONTROLLER;

    }

    public void startGame(GameContext gc)  {
        //TODO: extract the roles of the players and give it to the activity
        //TODO: extract every other information which were send by the server

        gameContext.copy(gc);
        startClientActivity.startGame();
        //wait some time before the gameactivity has been created
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gameActivity.outputMessage(R.string.progressBar_initial);
                gameActivity.longOutputMessage(R.string.gameStart_start);
                gameActivity.longOutputMessage(R.string.gameStart_hintRoles);

            }
        });

    }

    public void initiateWerewolfPhase() {
        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gameActivity.outputMessage(R.string.message_werewolfes_awaken);
                //TODO: put into string.xml with translation.. everything
                gameActivity.longOutputMessage("Die Werwölfe erwachen und suchen sich ein Opfer!");
                gameActivity.longOutputMessage("Macht euch bereit für die Abstimmung!");

            }
        });
    }


    public void endWerewolfPhase() {

        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gameActivity.longOutputMessage("Die Werwölfe haben ihr Opfer gefunden und schlafen wieder ein!");
                gameActivity.outputMessage(R.string.message_werewolfes_sleep);
                // TODO: only needed if GameMaster (GM) plays as well
                // go to the next state automatically (without GM interference)

                /*try {
                    NetworkPackage<GameContext.Phase> np = new NetworkPackage<GameContext.Phase>(NetworkPackage.PACKAGE_TYPE.DONE);
                    np.setPayload(GameContext.Phase.PHASE_WEREWOLF_END);
                    websocketClientHandler.send(np);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
        });

    }

    public void initiateWerewolfVotingPhase() {
        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int time = Integer.parseInt(gameContext.getSetting(GameContext.Setting.TIME_WEREWOLF));
                // TODO: there is an ASyncNetworkSocket exception when called here
                gameActivity.makeTimer(time).start();
                gameActivity.outputMessage(R.string.message_werewolfes_awaken);
                gameActivity.longOutputMessage("Die Werwölfe erwachen und suchen sich ein Opfer!");
                gameActivity.outputMessage(R.string.message_werewolfes_vote);
                //voting("Werewolf");

            }
        });
        if (!me.isDead() && me.getPlayerRole().equals(Player.Role.WEREWOLF)) {
            gameActivity.openVoting();
        } else {
            gameActivity.showTextPopup(R.string.voting_dialog_otherVotingTitle, R.string.voting_dialog_otherVoting);
        }

    }

    public void initiateCitzenVotingPhase() {
        if (!me.isDead() && me.getPlayerRole().equals(Player.Role.CITIZEN)) {
            gameActivity.openVoting();
        } else {
            gameActivity.showTextPopup(R.string.voting_dialog_otherVotingTitle, R.string.voting_dialog_otherVoting);
        }
    }


    public void initiateWitchPhase() {

        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int time = Integer.parseInt(gameContext.getSetting(GameContext.Setting.TIME_WITCH));
                gameActivity.makeTimer(time).start();
                // TODO: wenn die Hexe tot ist
                gameActivity.outputMessage(R.string.message_witch_awaken);
                gameActivity.longOutputMessage("Die Hexe erwacht!");
                gameActivity.longOutputMessage("Die Hexe entscheidet ob sie Tränke einsetzen möchte");
                useElixirs();
                gameActivity.longOutputMessage("Die Hexe hat ihre Entscheidung getroffen!");

                // TODO: only needed if GameMaster (GM) plays as well
                // go to the next state automatically (without GM interference)
                //websocketClientHandler.send("nextPhase");
                gameActivity.outputMessage(R.string.message_witch_sleep);
                gameActivity.longOutputMessage("Die Hexe schläft nun wieder ein");


                /*try {
                    NetworkPackage<GameContext.Phase> np = new NetworkPackage<GameContext.Phase>(NetworkPackage.PACKAGE_TYPE.DONE);
                    np.setPayload(GameContext.Phase.PHASE_WITCH);
                    websocketClientHandler.send(np);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
        });

    }

    public void initiateSeerPhase() {

        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int time = Integer.parseInt(gameContext.getSetting(GameContext.Setting.TIME_SEER));
                gameActivity.makeTimer(120).start();
                // TODO: wenn die Hexe tot ist
                gameActivity.outputMessage(R.string.message_seer_awaken);
                gameActivity.longOutputMessage("Die Seherin erwacht!");
                gameActivity.longOutputMessage("Die Seherin wählt einen Spieler aus, dessen Karte sie sich ansehen möchte");
                useSeerPower();
                gameActivity.longOutputMessage("Die Seherin kennt jetzt ein Geheimnis mehr!");

                // TODO: only needed if GameMaster (GM) plays as well
                // go to the next state automatically (without GM interference)
                //websocketClientHandler.send("nextPhase");
                gameActivity.outputMessage(R.string.message_seer_sleep);
                gameActivity.longOutputMessage("Die Seherin schläft nun wieder ein");


                /*try {
                    NetworkPackage<GameContext.Phase> np = new NetworkPackage<GameContext.Phase>(NetworkPackage.PACKAGE_TYPE.DONE);
                    np.setPayload(GameContext.Phase.PHASE_SEER);
                    websocketClientHandler.send(np);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
        });

    }

    public void initiateDayPhase() {

        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int time = Integer.parseInt(gameContext.getSetting(GameContext.Setting.TIME_VILLAGER));
                gameActivity.makeTimer(time).start();
                // TODO: wenn die Hexe tot ist
                gameActivity.outputMessage(R.string.message_villagers_awaken);
                gameActivity.longOutputMessage("Es wird hell und alle Dorfbewohner erwachen aus ihrem tiefen Schlaf");
                gameActivity.longOutputMessage("Leider von uns gegangen sind...");
                /*String[] deceasedPlayers = new String[gameContext.getNumberOfCasualties()];
                fillDeathList(deceasedPlayers);
                for(int i=0;i<deceasedPlayers.length;i++) {
                    gameActivity.longOutputMessage(deceasedPlayers[i]);
                }*/
                gameActivity.outputMessage(R.string.message_villagers_vote);
                gameActivity.longOutputMessage("Die übrigen Bewohner können jetzt abstimmen.");

            }
        });


    }

    public void endDayPhase() {
        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gameActivity.longOutputMessage("Die Abstimmung ist beendet...");
                // TODO: Detaillierte Sprachausgabe: solche Details auch in die Ausgabe? (finde ich zu viel)
                gameActivity.longOutputMessage("Hans wurde ausgewählt, und er ist...ein Horst!");


                // TODO: only needed if GameMaster (GM) plays as well
                // go to the next state automatically (without GM interference)
                //websocketClientHandler.send("nextPhase");
                gameActivity.outputMessage(R.string.message_villagers_sleep);
                gameActivity.longOutputMessage("Alle schlafen wieder ein, es wird Nacht!");

                /*try {
                    NetworkPackage<GameContext.Phase> np = new NetworkPackage<GameContext.Phase>(NetworkPackage.PACKAGE_TYPE.DONE);
                    np.setPayload(GameContext.Phase.PHASE_DAY_END);
                    websocketClientHandler.send(np);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
        });

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
        Log.d(TAG, "Hexe setzt ihre Fähigkeit ein");
        /*gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gameActivity.showElixirs();
                // TODO: implement Witch logic
            }
        });*/
    }

    public void useSeerPower() {
        Log.d(TAG, "Seherin setzt ihre Fähigkeit ein");
        // TODO: implement Seer logic
    }

    private List<Player> extractPlayers(String playerString) {
        ArrayList<Player> players = new ArrayList<>();
        String cuttedPlayers = playerString.replace("startGame_", " ").trim();

        String[] playerArray = cuttedPlayers.split("&");
        for (String playerNameString : playerArray) {
            Player p = new Player();
            p.setName(playerNameString);
            players.add(p);
        }

        return players;
    }


    public void sendVotingResult(Player player) {
        try {
            NetworkPackage<String> np = new NetworkPackage<String>(NetworkPackage.PACKAGE_TYPE.VOTING_RESULT);
            np.setPayload(player.getPlayerName());
            websocketClientHandler.send(np);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void handleVotingResult(String playerName) {

        Log.d(TAG, "voting_result received. Kill this guy: " + playerName);
        final Player playerToKill = GameContext.getInstance().getPlayerByName(playerName);
        playerToKill.setDead(true);

        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gameActivity.renderButtons();
                gameActivity.showTextPopup("Voting result", "The voting result is: "+ playerToKill.getPlayerName());
            }
        });

    }


    public void connect(String url, String playerName) {
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

    public Player getMyPlayer() {
        return gameContext.getPlayerById(myId);
    }

    public long getMyPlayerId() {
        return myId;
    }

    public void setMyId(long myId) {
        this.myId = myId;
    }

    public void updateMe() {
        this.me = gameContext.getPlayerById(this.myId);
        Log.d(TAG, "Me is now: " + me.getPlayerName() + "  isDead?: " + me.isDead());
    }
}
