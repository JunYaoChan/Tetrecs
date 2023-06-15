package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBlock;


/**
 * When the board is right-clicked, rotate the current piece
 */
public interface RightClickedListener {

     /**
      * Rotate the game block
      * @param gameBlock current game block
      */
     void rightClicked(GameBlock gameBlock);


}
