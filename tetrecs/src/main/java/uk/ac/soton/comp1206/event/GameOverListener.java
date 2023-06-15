package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.Game;

/**
 *  Handles game when the game is over
 */
public interface GameOverListener {

    /**
     * Handles when a game has over
     * @param game current game
     */
    void gameOver(Game game);
    }
