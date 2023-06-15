package uk.ac.soton.comp1206.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.App;

/**
 * The Grid is a model which holds the state of a game board. It is made up of a set of Integer values arranged in a 2D
 * arrow, with rows and columns.
 *
 * Each value inside the Grid is an IntegerProperty can be bound to enable modification and display of the contents of
 * the grid.
 *
 * The Grid contains functions related to modifying the model, for example, placing a piece inside the grid.
 *
 * The Grid should be linked to a GameBoard for it's display.
 */
public class Grid {

    /**
     * The number of columns in this grid
     */
    private final int cols;
    private static final Logger logger = LogManager.getLogger(Grid.class);

    /**
     * The number of rows in this grid
     */
    private final int rows;

    /**
     * The grid is a 2D arrow with rows and columns of SimpleIntegerProperties.
     */
    private final SimpleIntegerProperty[][] grid;

    /**
     * Create a new Grid with the specified number of columns and rows and initialise them
     * @param cols number of columns
     * @param rows number of rows
     */
    public Grid(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create the grid itself
        grid = new SimpleIntegerProperty[cols][rows];

        //Add a SimpleIntegerProperty to every block in the grid
        for(var y = 0; y < rows; y++) {
            for(var x = 0; x < cols; x++) {
                grid[x][y] = new SimpleIntegerProperty(0);
            }
        }
    }

    /**
     * Get the Integer property contained inside the grid at a given row and column index. Can be used for binding.
     * @param x column
     * @param y row
     * @return the IntegerProperty at the given x and y in this grid
     */
    public IntegerProperty getGridProperty(int x, int y) {
        return grid[x][y];
    }

    /**
     * Update the value at the given x and y index within the grid
     * @param x column
     * @param y row
     * @param value the new value
     */
    public void set(int x, int y, int value) {
        grid[x][y].set(value);
    }

    /**
     * Get the value represented at the given x and y index within the grid
     * @param x column
     * @param y row
     * @return the value
     */
    public int get(int x, int y) {
        try {
            //Get the value held in the property at the x and y index provided
            return grid[x][y].get();
        } catch (ArrayIndexOutOfBoundsException e) {
            //No such index
            return -1;
        }
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
     * Check the piece can placed in the current position
     * @param gamePiece current game piece
     * @param xValue x coordinates of the block
     * @param yValue y coordinates of the block
     * @return whether the piece is playable
     */
    public boolean canPlayPiece(GamePiece gamePiece, int xValue, int yValue) {

        int[][] gridBlocks = gamePiece.getBlocks();
        xValue-=1;
        yValue-=1;
        //nested for loop to iterate through the coordinates of the grid block
        for (int x = 0; x < gridBlocks.length; x++) {
            for (int y = 0; y < gridBlocks[x].length; y++) {
                int blockValue = gridBlocks[x][y];
                if (blockValue != 0) {
                    int gridValue = get(x + xValue, y + yValue);
                    if (gridValue != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * places the piece for the specified x and y values
     * @param gamePiece the game piece to be placed
     * @param xValue x coordinate of the piece
     * @param yValue y coordinate of the piece
     * @return true if the piece can be placed false if not
     */
    public boolean playPiece(GamePiece gamePiece, int xValue, int yValue) {
        int[][] gridBlocks = gamePiece.getBlocks();

        //if the piece cannot be played then return false
        if (!canPlayPiece(gamePiece, xValue, yValue))
            return false;
        else {
            xValue-=1;
            yValue-=1;
            for (int x = 0; x < gridBlocks.length; x++) {
                for (int y = 0; y < gridBlocks[x].length; y++) {
                    int blockValue = gridBlocks[x][y];

                    if (blockValue != 0){
                        set(x + xValue , y + yValue , blockValue);
                    }
                }
            }
        }

        return true;
    }

    /**
     * Set the block to zero after line is cleared
     */
    public void clear() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.grid[i][j].set(0);
            }
        }
    }

}
