package uk.ac.soton.comp1206.game;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.event.*;
import uk.ac.soton.comp1206.utility.Multimedia;

import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

    private static final Logger logger = LogManager.getLogger(Game.class);

    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;

    /**
     * The grid model linked to the game
     */
    protected final Grid grid;

    /**
     * Store the current piece
     */
    protected GamePiece currentPiece;
    /**
     * Store the following piece
     */
    protected GamePiece followingPiece;

    /**
     * Executor to execute game loop
     */
    protected ScheduledExecutorService timer;

    /**
     * Schedule a new loop
     */
    protected ScheduledFuture loop;

    /**
     * Multimedia class is used to play sounds or background music
     */
    protected Multimedia multimedia = new Multimedia();

    /**
     * Store the current score
     */
    protected SimpleIntegerProperty score = new SimpleIntegerProperty(0);

    /**
     * Store the current lives
     */
    protected SimpleIntegerProperty lives = new SimpleIntegerProperty(0);

    /**
     * Store the current level
     */
    protected SimpleIntegerProperty level = new SimpleIntegerProperty(0);

    /**
     * Store the current multiplier
     */
    protected SimpleIntegerProperty multiplier = new SimpleIntegerProperty(1);

    // Listeners
    /**
     * Handle right-clicked event
     */
    protected NextPieceListener nextPieceListener;

    /**
     * Assigns a listener for line clearing in the game
     */
    protected LineClearedListener lineClearedListener;
    /**
     * Assigns a listener to listen the game loop
     */
    protected GameLoopListener gameLoopListener;
    /**
     * Assigns a listener for detecting whether the game is over
     */
    protected GameOverListener gameOverListener;

    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols,rows);
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
        newLoop();


    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");
        this.lives.set(3);
        timer = Executors.newSingleThreadScheduledExecutor();
        currentPiece = spawnPiece();
        followingPiece = spawnPiece();
        nextPiece();



    }
    /**
     * Spawn a random new piece
     * @return the next piece to be played
     */
    public GamePiece spawnPiece() {
        logger.info("Spawn Piece");
        Random random = new Random();
        return GamePiece.createPiece(random.nextInt(15));
    }

    /**
     * Get the next piece of the game
     */
    public void nextPiece(){
        logger.info("Next Piece");
        currentPiece=followingPiece;
        followingPiece = spawnPiece();
        nextPieceListen();

    }

    /**
     * Run next piece
     */
    public void nextPieceListen(){
        if(nextPieceListener!=null){
            nextPieceListener.nextPiece(currentPiece,followingPiece);
        }
    }

    /**
     * Set Next Piece Listener
     * @param listener next piece listener
     */
    public void setNextPieceListener(NextPieceListener listener) {
        this.nextPieceListener = listener;
    }

    /**
     * Set Line Cleared Listener
     * @param lineClearedListener line clear listener
     */
    public void setLineClearedListener(LineClearedListener lineClearedListener) {
        this.lineClearedListener = lineClearedListener;
    }

    /**
     * Set Game Loop Listener
     * @param gameLoopListener game loop listener
     */
    public void setGameLoopListener(GameLoopListener gameLoopListener){
        this.gameLoopListener = gameLoopListener;
    }

    /**
     * Set Game Over Listener
     * @param gameOverListener game over listener
     */
    public void setGameOverListener(GameOverListener gameOverListener){
        this.gameOverListener = gameOverListener;
    }

    /**
     * Handle what should happen when a particular block is clicked
     * @param gameBlock the block that was clicked
     * @return boolean
     */
    public boolean blockClicked(GameBlock gameBlock) {
        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();

    
        //Check the block is playable
        if(grid.canPlayPiece(currentPiece, x, y)) {
            grid.playPiece(currentPiece, x, y);
            afterPiece();
            nextPiece();
            Multimedia.playSound("place.wav");
            return true;
        } else {
            Multimedia.playSound("fail.wav");
            return false;
        }
    }
    /**
     * Handles after a piece is placed
     */
    public void afterPiece(){
        int line = 0;
        
        //Save the blocks so they can be cleared afterwards
        HashSet<GameBlockCoordinate> blockCleared = new HashSet<>();
      
        //Search the lines to be cleared of vertical line
        for(int x=0; x < cols; x++) {
            int valueX = 0;
            for(int y=0; y < rows; y++) {
                if(grid.get(x,y) == 0) break;
                valueX+=1;
            }
            if(valueX == rows) {
                line+=1;
                for(int y=0; y < rows; y++) {
                    blockCleared.add(new GameBlockCoordinate(x,y));
                }
            }
        }

        //Search the lines to be cleared of horizontal line
        for(int y=0; y < rows; y++) {
            int valueY = 0;
            for(int x=0; x < cols; x++) {
                if(grid.get(x,y) == 0) break;
                valueY+=1;
            }
            if(valueY == cols) {
                line+=1;
                for(int x=0; x < cols; x++) {
                    blockCleared.add(new GameBlockCoordinate(x,y));
                }
            }
        }
        int totalBlocks = blockCleared.size();

      // add the multiplier
        if(line!=0){
            // add the multiplier by 1
            multiplier.set(multiplier.get()+1);
            // clear the line
            clearLine(blockCleared);
            //update the score
            score(line,totalBlocks);
            //listen to the line cleared
            if (lineClearedListener != null) {
                lineClearedListener.lineCleared(blockCleared);
            }
        }else{
            //set the multiplier if there is no line to be cleared
            multiplier.set(1);
        }
    }

    /**
     * Calculate the score of the game based on the lines, blocks and the multiplier
     * @param lines store the number of cleared lines
     * @param blocks store the number of cleared blocks
     */
    public void score(int lines, int blocks){
        logger.info("Add Points");
        int newScore = lines * blocks * 10 * multiplier.get();
        this.score.set(score.get()+newScore);
        logger.info(newScore);
        level.set(score.get()/1000);



    }

    /**
     * Loop through the HashSet to clear the line
     * @param blockCoordinates : HashSet that contains the coordinates of the blocks
     */
    public void clearLine(HashSet<GameBlockCoordinate> blockCoordinates){
        logger.info("Clearing the lines");
        for(GameBlockCoordinate block :blockCoordinates){
            this.grid.set(block.getX(),block.getY(),0);
        }
    }
    /**
     * Rotate the currentPiece
     */
    public void rotateCurrentPiece(){
        logger.info("Rotate");
        currentPiece.rotate();

    }

    /**
     * Swaps the currentPiece and the followingPiece
     */
    public void swapCurrentPiece(){
        logger.info("Swap to the next piece");
        GamePiece tmp = this.currentPiece;
        this.currentPiece = this.followingPiece;
        this.followingPiece = tmp;


    }


    /**
     * Get the total duration of the timer
     * @return time
     */
    public int getTimerDelay(){
        return Math.max(2500, 12000 - 500 * level.get());

    }

    /**
     * Creates the game loop
     */
    public void gameLoop(){
        logger.info("Game Loop");
        nextPiece();
        if(lives.get()>0){
            //if pieces do not placed then lost 1 life
            logger.info("Lose life");
            this.lives.set(this.lives.get()-1);
            logger.info("lives : " + lives.get());
            multiplier.set(1);
            multimedia.playSound("lifelose.wav");
        }else{ //if lives reach -1 stop the game
            this.lives.set(this.lives.get()-1);
            logger.info("lives : " + lives.get());
            if(gameOverListener != null) {
                logger.info("Game Over");
                logger.info("lives : "+ lives.get());
                timer.shutdownNow();
                Multimedia.stopBackgroundMusic();
                Platform.runLater(() -> gameOverListener.gameOver(this));
            }
        }

        if(gameLoopListener != null){
            gameLoopListener.gameLoop(getTimerDelay());
        }
        newLoop();
    }

    /**
     * Starts a new game loop
     */
    public void newLoop(){
        loop = timer.schedule(this::gameLoop, getTimerDelay(), TimeUnit.MILLISECONDS);
        if(gameLoopListener != null){
            gameLoopListener.gameLoop(getTimerDelay());
        }
    }

    /**
     * Restarts the game loop
     */
    public void resetGameLoop(){
        loop.cancel(false);
        newLoop();
    }


    /**
     * Stop the timer
     */
    public void gameEnded(){
        logger.info("Game Stopped");
        timer.shutdownNow();
    }
    /**
     * Get the grid model inside this game representing the game state of the board
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Get the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }



    /**
     * Call the player's current lives' property
     * @return lives' property
     */
    public IntegerProperty getLivesProperty() {
        return this.lives;
    }

    /**
     * Call the player's current levels' property
     * @return levels' property
     */
    public SimpleIntegerProperty getLevelProperty() {
        return this.level;
    }

    /**
     * Call the player's current scores' property
     * @return score's property
     */
    public SimpleIntegerProperty getScoreProperty() {
        return this.score;
    }


    /**
     * Call the player's current multiplier' property
     * @return multiplier's property
     */
    public SimpleIntegerProperty getMultiplierProperty() {
        return this.multiplier;
    }

    /**
     * Call the player's current piece
     * @return current piece
     */
    public GamePiece getCurrentPiece(){ return this.currentPiece;}

    /**
     * Call the player's following piece
     * @return following piece
     */
    public GamePiece getFollowingPiece(){ return this.followingPiece;}
}
