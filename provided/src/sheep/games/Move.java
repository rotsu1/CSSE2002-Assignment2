package sheep.games;

import sheep.ui.Perform;
import sheep.ui.Prompt;

/**
 * Move class which handles the shifts of the block.
 */
public class Move<T extends MoveCell> implements Perform {

    /**
     * The direction to shift the block.
     */
    private int direction;

    /**
     * Reference to game.
     */
    private T game;

    /**
     * Constructor
     * @param direction the direction to shift the block.
     * @param game Game reference.
     */
    public Move(int direction, T game) {
        this.direction = direction;
        this.game = game;
    }

    /**
     * Shifts the block by the given direction.
     * @param row The currently selected row of the user, or -2 if none
     *           selected.
     * @param column The currently selected column of the user, or -2 if none
     *              selected.
     * @param prompt Provides a mechanism to interact with the user interface
     *               after an interaction, if required.
     */
    @Override
    public void perform(int row, int column, Prompt prompt) {
        game.shift(direction);
    }
}