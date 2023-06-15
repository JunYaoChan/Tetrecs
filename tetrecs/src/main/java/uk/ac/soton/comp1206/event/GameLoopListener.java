package uk.ac.soton.comp1206.event;

/**
 *  GameLoopListener updates the timer
 */
public interface GameLoopListener {

    /**
     * Set the timer duration
     * @param duration total time
     */
    void gameLoop(int duration);
}
