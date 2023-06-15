package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.Leaderboard;
import uk.ac.soton.comp1206.component.ScoreList;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;

import java.io.*;
import java.util.*;

/**
 * Shows all the scores after the game
 */
public class ScoresScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(ScoresScene.class);

    /**
     * Plays sound effect and music background
     */
    protected Multimedia multimedia = new Multimedia();
    /**
     * Receive and send message to the server
     */
    protected Communicator communicator;
    /**
     * Current game
     */
    protected Game game;
    /**
     * Store the username
     */
    protected String username;
    /**
     * Store the box
     */
    protected BorderPane box;

    /**
     * Store the local score list 
     */
    protected SimpleListProperty<Pair<String, Integer>> localScores = new SimpleListProperty();
    /**
     * Store the remote score list
     */
    protected SimpleListProperty<Pair<String, Integer>> remoteScores = new SimpleListProperty();
    /**
     * Check whether is multiplayer game or not
     */
    protected Boolean multiplayer;
    /**
     * Store the multiplayer score list
     */
    protected SimpleListProperty<Pair<String, Integer>> multiplayerScores = new SimpleListProperty<>();
    /**
     * Initialise the online score list
     */
    protected SimpleListProperty<Pair<String, Integer>> onlineScores;

    /**
     * Display the game score list in real time
     */
    protected Leaderboard multiplayerScoreList;
    /**
     * Display the local score list with animation
     */
    protected ScoreList localScoreList;
    /**
     * Exit Button
     */
    protected Button exitButton;

    /**
     * Create a Score scene, passing in the GameWindow the scene will be displayed in
     * @param gameWindow the game window
     * @param game game
     * @param multiplayer check whether is Multiplayer game
     */
    public ScoresScene(GameWindow gameWindow, Game game,Boolean multiplayer) {
        super(gameWindow);
        logger.info("Create Score Scene");
        this.communicator = gameWindow.getCommunicator();
        this.localScores.set(FXCollections.observableArrayList(new ArrayList<Pair<String, Integer>>()));
        this.remoteScores.set(FXCollections.observableArrayList(new ArrayList<Pair<String, Integer>>()));
        this.game = game;
        this.multiplayer =multiplayer;



    }

    /**
     * Create a Score scene, passing in the GameWindow the scene will be displayed in
     * @param gameWindow the game window
     * @param game game
     * @param multiplayer check whether is Multiplayer game
     * @param onlineScores pass all the score in the multiplayer game
     */
    public ScoresScene(GameWindow gameWindow, Game game,Boolean multiplayer,SimpleListProperty<Pair<String, Integer>> onlineScores) {
        super(gameWindow);
        logger.info("Loading Multiplayer Scores Scene");
        logger.info("Create Score Scene");
        this.communicator = gameWindow.getCommunicator();
        logger.info(onlineScores.get(0));
        this.onlineScores = onlineScores;
        this.multiplayer =multiplayer;
        this.localScores.set(FXCollections.observableArrayList(new ArrayList<Pair<String, Integer>>()));
        this.remoteScores.set(FXCollections.observableArrayList(new ArrayList<Pair<String, Integer>>()));
        this.game = game;



    }


    /**
     * Initialise the scene
     */
    @Override
    public void initialise() {
        logger.info("Running Score Scene");
        Multimedia.playBackgroundMusic("end.wav");
        communicator.addListener((message) -> {
                if (message.startsWith("HISCORES")) {
                    Platform.runLater(() -> loadOnlineScores(message));}

        });
        communicator.send("HISCORES");
        if(multiplayer){
            multiplayerScores.addAll(onlineScores);
        }





    }


    /**
     * Handle whether is local or multiplayer score so that it can be written in correct file
     */
    protected void updateLocalScores() {
        if(multiplayer){
            //Sort the score
            Collections.sort(multiplayerScores, (score1, score2) -> score2.getValue().compareTo(score1.getValue()));
            writeScores("multiplayerScore.txt");
        }else{
            //Add personal score
            localScores.add(new Pair<String, Integer>(this.username, game.getScoreProperty().get()));
            Collections.sort(localScores, (score1, score2) -> score2.getValue().compareTo(score1.getValue()));
            writeScores("scores.txt");


        }

    }

    /**
     * Build the layout of the scene
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var scorePane = new StackPane();
        scorePane.setMaxWidth(gameWindow.getWidth());
        scorePane.setMaxHeight(gameWindow.getHeight());
        scorePane.getStyleClass().add("score-background");
        root.getChildren().add(scorePane);

        var mainPane = new BorderPane();
        scorePane.getChildren().add(mainPane);

        //Title Box
        var titleBox = new HBox();
        var title = new Text("Game Over");
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().add(title);
        title.getStyleClass().add("bigtitle");
        mainPane.setTop(titleBox);

        //main box
        box = new BorderPane();

        //Local Leaderboard
        var localBox = new VBox();
        localBox.setSpacing(8);
        box.setLeft(localBox);
        localBox.setTranslateX(30);
        var localTitle = new Text("Local Scores");
        localTitle.getStyleClass().add("heading2");
        localScoreList = new ScoreList();
        multiplayerScoreList = new Leaderboard();
        localScores.bind(localScoreList.getScoresProperty());
        multiplayerScores.bind(multiplayerScoreList.getScoresProperty());
        localBox.getChildren().addAll(localTitle, localScoreList,multiplayerScoreList);


        //Online Leaderboard
        var remoteBox = new VBox();
        box.setRight(remoteBox);
        remoteBox.setSpacing(8);
        remoteBox.setTranslateX(-30);
        var remoteScoreList = new ScoreList();
        var onlineTitle = new Text("Online Scores");
        onlineTitle.getStyleClass().add("heading2");
        remoteScores.bind(remoteScoreList.getScoresProperty());
        remoteBox.getChildren().addAll(onlineTitle, remoteScoreList);


//      Insert Username for local score
        var userBox = new VBox();
        var name = new TextField();
        name.setMaxWidth(gameWindow.getWidth()/2);
        userBox.setAlignment(Pos.CENTER);
        mainPane.setCenter(userBox);
        Text text = new Text("Enter your name : ");
        text.getStyleClass().add("menuItem");
        name.setPromptText("username");
        var button = new Button("Submit");
        button.getStyleClass().add("buttonName1");
        userBox.setSpacing(10);
        userBox.getChildren().addAll(text,name,button);
        button.setOnAction(event ->{
            if(name.getText()!=null) {
                username = name.getText();
                userBox.setVisible(false);
                userBox.getChildren().clear();
                loadScores();
                updateLocalScores();
                mainPane.setCenter(box);
                mainPane.setBottom(exitButton);

            }else{
                username = "Unknown";
            }
        } );


        //Exit Button
        exitButton = new Button("Exit to Menu");
        exitButton.setOnAction(event -> {
            multimedia.stopBackgroundMusic();
            gameWindow.startMenu();
        });
        exitButton.setBackground(null);
        BorderPane.setAlignment(exitButton,Pos.CENTER);
        exitButton.getStyleClass().add("buttonName1");
        exitButton.setTranslateY(-80);

        if(multiplayer){
            updateLocalScores();
            userBox.setVisible(false);
            userBox.getChildren().clear();
            mainPane.setCenter(box);
            mainPane.setBottom(exitButton);

        }




    }

    /**
     * Load the offline score from the scores.txt file
     */
    protected void loadScores() {
            try {
                File file = new File("scores.txt");
                if (!file.exists()) {
                    writeScores("scores.txt");
                } else {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] arr = line.split(":");
                        localScores.add(new Pair<String, Integer>(arr[0], Integer.parseInt(arr[1])));
                    }
                    reader.close();
                }
            } catch (Exception e) {
                logger.error("File not found");
                e.printStackTrace();
            }


    }

    /**
     * Write the local score into the specific
     * @param filename determine which file to write
     */
    protected void writeScores(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            if(multiplayer){
                for (Pair<String, Integer> pair : multiplayerScores) {
                    String username = pair.getKey();
                    String score = pair.getValue().toString();
                    String info = username + ":" + score + "\n";
                    writer.write(info);
                }

            }else{
                for (Pair<String, Integer> pair : localScores) {
                    String username = pair.getKey();
                    String score = pair.getValue().toString();
                    String info = username + ":" + score + "\n";
                    writer.write(info);
                }

            }


            writer.close();

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    /**
     * Handles top 10 highest scorer and update it into the remote score list
     * @param message contains all the first 10 highest scorer when HISOCRES is received
     */
    protected void loadOnlineScores(String message) {
        logger.info("Receive Online Scores : " + message);
        message = message.replace("HISCORES", "");
        var scores = message.split("\n");
        for (String score : scores) {
            var parts = score.split(":");
            if (parts.length < 2) continue;
            var name = parts[0];
            var onlineScores = parts[1];
            remoteScores.add(new Pair<>(name, Integer.parseInt(onlineScores)));
            Collections.sort(remoteScores, (score1, score2) -> score2.getValue().compareTo(score1.getValue()));
            logger.info("Received score: {} = {}", name, onlineScores);

            }
        if(game.getScoreProperty().get()>remoteScores.get(9).getValue()){
            writeOnlineSocres();
        }

    }

    /**
     * Sends the score if the player have exceeded the score more than the top 10 scorer
     */
    protected void writeOnlineSocres() {
        communicator.send("HISCORE " + this.username + ":" + this.game.getScoreProperty().get());
    }
}
