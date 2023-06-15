package uk.ac.soton.comp1206.game;

import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;

/**
 * MultiplayerGame extends the Game class
 */
public class MultiplayerGame extends Game{

    private static final Logger logger = LogManager.getLogger(MultiplayerGame.class);

    /**
     * Receive and send messages to the server
     */
    protected Communicator communicator;

    /**
     * Game is not initialised
     */
    protected boolean ready = false;
    /**
     * Store the game pieces generated from the server
     */
    Queue<GamePiece> queue = new LinkedList<>();

    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     *
     * @param cols         number of columns
     * @param rows         number of rows
     * @param communicator receive or send messages from the server
     */
    public MultiplayerGame(int cols, int rows, Communicator communicator) {
        super(cols, rows);
        this.communicator = communicator;

    }

    /**
     * Initialise a new Multiplayer game
     */
    public void initialiseGame(){
        logger.info("Initialise Multiplayer Game");
        timer = Executors.newSingleThreadScheduledExecutor();
        communicator.addListener((message)->{
        Platform.runLater(()->handleMessage(message.trim()));});
        this.lives.set(3);
        //send the initial pieces for the game to start
        for (int i = 0; i < 10; i++)
            this.communicator.send("PIECE");

    }
    /**
     * Handles the messages from the communicator
     * @param message information from the server
     */
    protected void handleMessage(String message) {
        if(message.contains("PIECE ")){
            var msg = message.replace("PIECE ","");
            logger.info(msg);
            GamePiece gamePiece = GamePiece.createPiece(Integer.parseInt(msg));
            queue.add(gamePiece);
            //initialise the pieces
            if (!ready && queue.size() > 2) {
                communicator.send("PIECE");
                followingPiece = queue.poll();
                nextPiece();
                ready = true;
            }
        }

    }
    /**
     * Assign the given piece to current and remove the piece from the queue
     */
    @Override
    public void nextPiece() {
        currentPiece = followingPiece;
        followingPiece = queue.remove();
        nextPieceListener.nextPiece(currentPiece, followingPiece);
        communicator.send("PIECE");
    }

    /**
     * Send the current score to the server
     */
    @Override
    public void score(int lines, int blocks) {
        super.score(lines, blocks);
        communicator.send("SCORE "+getScoreProperty().get());
    }

}
