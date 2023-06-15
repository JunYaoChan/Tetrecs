package uk.ac.soton.comp1206.scene;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.*;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;

import java.io.*;
import java.util.ArrayList;
import java.util.Set;
import java.util.Stack;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(ChallengeScene.class);
    /**
     * Current game
     */
    protected Game game;
    /**
     * Current highest score of the game
     */
    protected SimpleIntegerProperty highestScore = new SimpleIntegerProperty();

    /**
     * Store the current game piece board
     */
    protected PieceBoard currentPieceboard;
    /**
     * Store the following game piece board
     */
    protected PieceBoard nextPieceboard;
    /**
     * Store the current game board
     */
    protected GameBoard board;
    /**
     * TimeBar - represent the progressbar that count down the time
     */
    protected ProgressBar timeBar = new ProgressBar();
    /**
     * Set the TimeBar animation during a game loop
     */
    protected Timeline timeline;
    /**
     * Store the highest score value in a hbox
     */
    protected HBox highBox;
    /**
     * Contains all the piece board
     */
    protected BorderPane piecesBoard;
    /**
     * Contains all the piece board
     */
    protected VBox pieces;
    /**
     * Contain the main board
     */
    protected StackPane playBox;
    /**
     * Play sound effect and background music
     */
    protected Multimedia multimedia = new Multimedia();
    /**
     * The initial x coordinate of the block
     */
    protected int keyX = 0;
    /**
     * The initial y coordinate of the block
     */
    protected int keyY = 0;




    /**
     * Create a new Single Player challenge scene
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Challenge Scene");
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        setupGame();

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("challenge-background");
        root.getChildren().add(challengePane);

        var mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);


        //Main Board
        playBox = new StackPane();
        board = new GameBoard(game.getGrid(),gameWindow.getWidth()/2,gameWindow.getWidth()/2);
        board.setTranslateX(-45);
        board.setTranslateY(80);
        playBox.setAlignment(board,Pos.TOP_CENTER);
        playBox.getChildren().add(board);
        mainPane.setCenter(playBox);



        //All records boxes
        var allBoxes = new HBox();
        allBoxes.setSpacing(15);
        allBoxes.setTranslateX(5);
        allBoxes.setTranslateY(10);


        //Score Box
        var scoreBox = new HBox();
        var scoreTitle = new Text("Score : ");
        var scoreLabel = new Text();
        scoreLabel.textProperty().bind(game.getScoreProperty().asString());
        scoreBox.getChildren().addAll(scoreTitle,scoreLabel);
        scoreBox.setAlignment(Pos.TOP_LEFT);
        scoreTitle.getStyleClass().add("heading");
        scoreLabel.getStyleClass().add("heading");

        //Lives Box
        var livesBox = new HBox();
        var livesTitle = new Text("Lives : ");
        var livesLabel = new Text();
        livesLabel.textProperty().bind(game.getLivesProperty().asString());
        livesBox.getChildren().addAll(livesTitle,livesLabel);
        livesTitle.getStyleClass().add("heading");
        livesLabel.getStyleClass().add("heading");

        //Level Box
        var levelBox = new HBox();
        var levelTitle = new Text("Level : ");
        var levelLabel = new Text();
        levelLabel.textProperty().bind(game.getLevelProperty().asString());
        levelBox.getChildren().addAll(levelTitle,levelLabel);
        levelTitle.getStyleClass().add("heading");
        levelLabel.getStyleClass().add("heading");

        //Multiplier Box
        var multiplierBox = new HBox();
        var multiplierTitle = new Text("Multiplier : ");
        var multiplierLabel = new Text();
        multiplierLabel.textProperty().bind(game.getMultiplierProperty().asString());
        multiplierBox.getChildren().addAll(multiplierTitle,multiplierLabel);
        multiplierTitle.getStyleClass().add("heading");
        multiplierLabel.getStyleClass().add("heading");


        allBoxes.getChildren().addAll(scoreBox,livesBox,levelBox,multiplierBox);
        mainPane.setTop(allBoxes);

        //Timer Bar
        timeBar.prefWidthProperty().set(gameWindow.getWidth());
        mainPane.setBottom(timeBar);
        timeBar.setStyle("-fx-accent: limegreen;");


        //Pieceboards
        piecesBoard = new BorderPane();
        pieces = new VBox();
        highBox = new HBox();
        currentPieceboard = new PieceBoard(3,3,200,200);
        nextPieceboard = new PieceBoard(3,3,130,130);
        nextPieceboard.setTranslateX(-40);
        currentPieceboard.center();
        nextPieceboard.center();
        pieces.getChildren().addAll(highBox,currentPieceboard,nextPieceboard);
        piecesBoard.setCenter(pieces);
        pieces.setAlignment(Pos.CENTER_RIGHT);
        pieces.setTranslateX(-85);
        pieces.setSpacing(25);
        mainPane.setRight(piecesBoard);

        //Highest Score Box
        var highScoreTitle = new Text("Highest Score : ");
        var highScore = new Text();
        highScoreTitle.getStyleClass().add("heading");
        highScore.getStyleClass().add("heading");
        highScore.textProperty().bind(this.highestScore.asString());
        highBox.getChildren().addAll(highScoreTitle,highScore);
        highBox.setTranslateX(15);



    }

    /**
     * Updates the highest score when the player's score has exceeded it
     * @param observableValue observed value
     * @param oldValue old value
     * @param newValue new value
     */
    protected void getHighestScore(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
        if(highestScore.get()< game.getScoreProperty().get()){
            highestScore.set(game.getScoreProperty().get());
        }

    }

    /**
     * Get the highest offline score
     */
    protected void readScore() {
        File file = new File("scores.txt");
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine();
                if(line==null){
                    this.highestScore.set(0);
                }else {
                    String[] arr = line.split(":");
                    logger.info(Integer.parseInt(arr[1]));
                    this.highestScore.set(Integer.parseInt(arr[1]));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    /**
     * Set the timeBar animation in a game loop
     * @param duration total time
     */
    protected void gameLoop(int duration) {
        timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(timeBar.progressProperty(), 1)),
                new KeyFrame(Duration.millis(duration), e-> {
                }, new KeyValue(timeBar.progressProperty(), 0))

        );
        timeline.play();

    }

    /**
     * When the line is cleared, implement the fade out animation
     */
    private void lineCleared(Set<GameBlockCoordinate> gameBlockCoordinates) {
        board.fadeOut(gameBlockCoordinates);
        multimedia.playSound("clear.wav");
    }

    /**
     * Rotate the piece when the method is called
     * @param gameBlock current game block
     */
    protected void rightClicked(GameBlock gameBlock) {
        game.rotateCurrentPiece();
        currentPieceboard.display(game.getCurrentPiece());
    }

    /**
     * Display the piece when the method is called
     * @param currentgamePiece current game piece
     * @param nextGamePiece next game piece
     */
    protected void nextPiece(GamePiece currentgamePiece, GamePiece nextGamePiece) {
        currentPieceboard.display(currentgamePiece);
        nextPieceboard.display(nextGamePiece);
    }

    /**
     * Handle when a block is clicked
     * @param gameBlock the Game Block that was clocked
     */
    private void blockClicked(GameBlock gameBlock) {
        if(game.blockClicked(gameBlock)){
            game.resetGameLoop();
        }
    }

    /**
     * Swap the pieces when the following block is clicked
     * @param gameBlock current game block
     */
    protected void swapPiece(GameBlock gameBlock){
        swapPiece();

    }

    /**
     * Swap the current piece to the following piece and display the following piece on the current pieceBoard
     */
    private void swapPiece(){
        game.swapCurrentPiece();
        currentPieceboard.display(game.getCurrentPiece());
        nextPieceboard.display(game.getFollowingPiece());
        multimedia.playSound("transition.wav");

    }

    /**
     * Rotate the block when block in the current pieceBoard is clicked
     * @param gameBlock the Game Block that was clocked
     */
    private void rotatePiece(GameBlock gameBlock){
        Multimedia.playSound("rotate.wav");
        game.rotateCurrentPiece();
        currentPieceboard.display(game.getCurrentPiece());

    }
    /**
     * Setup the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");

        //Start new game
        game = new Game(5, 5);
    }

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        readScore();
        //Handle block on gameboard grid being clicked
        game.setNextPieceListener(this::nextPiece);
        board.setOnBlockClick(this::blockClicked);
        board.setOnRightClicked(this::rightClicked);
        currentPieceboard.setOnBlockClick(this::rotatePiece);
        nextPieceboard.setOnBlockClick(this::swapPiece);
        game.setLineClearedListener(this::lineCleared);
        game.setGameLoopListener(this::gameLoop);
        game.setGameOverListener(game-> {
            game.gameEnded();
            timeline.stop();
            gameWindow.startScore(this.game);
        });
        game.getScoreProperty().addListener(this::getHighestScore);
        game.start();
        this.multimedia.playBackgroundMusic("game.wav");
        scene.setOnKeyPressed(this::keyPressed);
    }

    /**
     * Handles all the events when a key is pressed
     * @param keyEvent is the keyboard key when is activated
     */
    protected void keyPressed(KeyEvent keyEvent) {
        board.getBlock(keyX, keyY).paint();
        if ((keyEvent.getCode().equals(KeyCode.UP) || keyEvent.getCode().equals(KeyCode.W)) && keyY > 0) {
            keyY-=1;
            board.getBlock(keyX,keyY).hover();

        } else if ((keyEvent.getCode().equals(KeyCode.DOWN) || keyEvent.getCode().equals(KeyCode.S)) && keyY < game.getRows() - 1) {
            keyY+=1;
            board.getBlock(keyX,keyY).hover();

        } else if ((keyEvent.getCode().equals(KeyCode.LEFT) || keyEvent.getCode().equals(KeyCode.A)) && keyX > 0) {
            keyX-=1;
            board.getBlock(keyX,keyY).hover();

        } else if ((keyEvent.getCode().equals(KeyCode.RIGHT)||  keyEvent.getCode().equals(KeyCode.D))  && keyX < game.getCols() - 1) {
            keyX+=1;
            board.getBlock(keyX,keyY).hover();
        }else if((keyEvent.getCode().equals(KeyCode.ESCAPE))){
            game.gameEnded();
            timeline.stop();
            Multimedia.stopBackgroundMusic();
            gameWindow.startMenu();

        }else if(keyEvent.getCode().equals(KeyCode.SPACE) || keyEvent.getCode().equals(KeyCode.R)){
            swapPiece();

        }else if (keyEvent.getCode().equals(KeyCode.Q) || keyEvent.getCode().equals(KeyCode.Z) || keyEvent.getCode().equals(KeyCode.OPEN_BRACKET)) {
            rotate(3);
        }
        else if (keyEvent.getCode().equals(KeyCode.E) || keyEvent.getCode().equals(KeyCode.C) || keyEvent.getCode().equals(KeyCode.CLOSE_BRACKET)) {
            rotate(1);
        }else if(keyEvent.getCode() == KeyCode.ENTER || keyEvent.getCode() == KeyCode.X) {
            blockClicked(board.getBlock(keyX, keyY));


        }
    }

    /**
     * Rotate left or right when the key is pressed
     * @param number number of rotations
     */
    protected void rotate(int number ){
        for(int x = 0; x< number; x++) {
            game.rotateCurrentPiece();
        }
        currentPieceboard.display(game.getCurrentPiece());
        multimedia.playSound("rotate.wav");

    }


}
