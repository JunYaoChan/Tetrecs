package uk.ac.soton.comp1206.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;

/**
 * Create the class that store the game piece in the small board
 */
public class PieceBoard extends GameBoard {

    private static final Logger logger = LogManager.getLogger(PieceBoard.class);


    /**
     * Create a new PieceBoard
     * @param cols number of columns
     * @param rows number of rows
     * @param width the visual width
     * @param height the visual height
     */
    public PieceBoard(int cols, int rows, double width, double height) {
        super(cols, rows, width, height);
    }

    /**
     * Display the current and following game piece
     * @param gamePiece store the pieces
     */
    public void display(GamePiece gamePiece){
        grid.clear();
        grid.playPiece(gamePiece,1,1);
    }

    /**
     * Set the point to the center of the piece
     */
    public void center() {
        this.getBlock(1,1).setCenter();
    }

}
