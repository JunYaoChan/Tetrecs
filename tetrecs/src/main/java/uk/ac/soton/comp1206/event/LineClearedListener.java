package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBlockCoordinate;

import java.util.Set;

/**
 * GameLoopListener is used when the line is cleared to play the animations
 */
public interface LineClearedListener {

    /**
     * Animates all the game block in the Set
     * @param gameBlockCoordinate store the game block coordinates
     */
    void lineCleared(Set<GameBlockCoordinate> gameBlockCoordinate);
}
