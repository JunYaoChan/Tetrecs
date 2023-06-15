package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.Leaderboard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Create a Multiplayer scene which extend the Challenge scene
 */
public class MultiplayerScene extends ChallengeScene {

    private static final Logger logger = LogManager.getLogger(MultiplayerScene.class);
    /**
     * Receive and send messages to the server
     */
    protected Communicator communicator;

    /**
     * Store the multiplayer score list
     */
    protected SimpleListProperty<Pair<String, Integer>> onlineScores = new SimpleListProperty<>();

    /**
     * Handles the visual appearance and function of the multiplayer scores
     */
    protected Leaderboard leaderBoard;

    /**
     * Holds the chat message in a vertical box
     */
    protected VBox chatBox;
    /**
     * TextField
     */
    protected TextField text;
    /**
     * Current game window
     */
    protected GameWindow gameWindow;

    /**
     * Count the text in the chatBox
     */
    protected int count = 0;


    /**
     * Create a new Single Player challenge scene
     *
     * @param gameWindow the Game Window
     */
    public MultiplayerScene(GameWindow gameWindow) {
        super(gameWindow);
        this.gameWindow = gameWindow;
        this.communicator = gameWindow.getCommunicator();
        this.onlineScores.set(FXCollections.observableArrayList(new ArrayList<Pair<String, Integer>>()));


    }

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        super.initialise();
        communicator.addListener(message -> Platform.runLater(() -> handleMessage(message)));
        communicator.send("SCORES");
        game.setGameOverListener(game -> {
            communicator.send("DIE");
            game.gameEnded();
            gameWindow.startMultiplayerScore(game, true, onlineScores);
        });




    }
    /**
     * Handles the action when the key is pressed
     * @param keyEvent holds the key=pressed
     */
    @Override
    protected void keyPressed(KeyEvent keyEvent) {
        super.keyPressed(keyEvent);
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
                Multimedia.stopBackgroundMusic();
                game.gameEnded();
                multimedia.playSound("transition.wav");
                gameWindow.startMenu();
                communicator.send("DIE");
        }else if (keyEvent.getCode() == KeyCode.TAB) {
            text.setPromptText("Enter message/Click Shift to exit");
            text.setDisable(false);

        }
    }
    /**
     * Build the layout
     */
    @Override
    public void build() {
        super.build();
        highBox.getChildren().clear();
        pieces.getChildren().clear();

        var piece = new VBox();
        piece.setSpacing(20);
        currentPieceboard = new PieceBoard(3, 3, 170, 170);
        nextPieceboard = new PieceBoard(3, 3, 100, 100);
        piece.setTranslateX(-30);
        currentPieceboard.setTranslateX(-25);
        currentPieceboard.center();
        nextPieceboard.center();
        piece.getChildren().addAll(currentPieceboard, nextPieceboard);
        piecesBoard.setCenter(piece);
        piece.setTranslateY(120);


        //Current players scores
        leaderBoard = new Leaderboard();
        onlineScores.bind(leaderBoard.getScoresProperty());
        piecesBoard.setTop(leaderBoard);
        leaderBoard.setTranslateY(70);
        leaderBoard.setTranslateX(-42);

        board.setTranslateX(-50);
        board.setTranslateY(60);
        chatBox = new VBox();
        chatBox.getStyleClass().add("messages");

        //TextField
        text = new TextField();
        text.setPromptText("Click Tab to type");
        text.setMaxWidth(gameWindow.getWidth()/2+60);
        chatBox.setTranslateY(470);
        playBox.setAlignment(chatBox,Pos.CENTER_LEFT);
        playBox.setAlignment(text,Pos.BOTTOM_LEFT);
        playBox.getChildren().addAll(chatBox,text);
        text.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                communicator.send("MSG " + text.getText());
                text.clear();

            } else if(keyEvent.getCode() == KeyCode.SHIFT){
                text.setDisable(true);
            }

        });
        text.setDisable(true);


    }
    /**
     * Handles messages that are received form the server
     * @param message holds the message
     */
    protected void handleMessage(String message) {
        if (message.startsWith("SCORES")) {
            var msg = message.replace("SCORES ", "").split("\n");
            this.onlineScores.clear();
            for (String messages : msg) {
                var data = messages.split(":");
                this.onlineScores.add(new Pair<>(data[0], Integer.parseInt(data[1])));
            }
            Collections.sort(onlineScores, (score1, score2) -> score2.getValue().compareTo(score1.getValue()));
        } else if (message.startsWith("MSG")) {
            if(count > 2){
                chatBox.getChildren().remove(0);
            }
            var msg = message.replace("MSG ", "").split(":");
            Text text = new Text(msg[0] + " : " + msg[1]);
            logger.info(msg[1]);
            text.getStyleClass().add("heading1");
            chatBox.getChildren().add(text);
            count++;
        } else if(message.contains("DIE")) {
            var msg = message.replace("DIE ", "");
            leaderBoard.userDIE(msg);
        }

}


    /**
     * Set up the Multiplayer game
     */
    public void setupGame() {
        logger.info("Starting a new multiplayer game");
        game = new MultiplayerGame(5,  5,communicator);
    }


}
