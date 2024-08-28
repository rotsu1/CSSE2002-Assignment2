package sheep.games;

import sheep.games.tetros.Tetros;
import sheep.ui.Perform;
import sheep.ui.Prompt;

/**
 * Rotate class which handles the rotation of the falling block.
 */
public class Rotate implements Perform {

    /**
     * The direction to rotate the block.
     */
    private final int direction;

    /**
     * Reference to Tetros.
     */
    private Tetros tetros;

    /**
     * Constructor
     * @param direction the direction to rotate the block.
     * @param tetros Tetros game reference.
     */
    public Rotate(int direction, Tetros tetros) {
        this.direction = direction;
        this.tetros = tetros;
    }

    /**
     * Rotates the block by the given direction.
     * @param row The currently selected row of the user, or -2 if none
     *           selected.
     * @param column The currently selected column of the user, or -2 if none
     *              selected.
     * @param prompt Provides a mechanism to interact with the user interface
     *               after an interaction, if required.
     */
    @Override
    public void perform(int row, int column, Prompt prompt) {
        if (!tetros.getStarted()) {
            return;
        }
        tetros.flip(direction);
    }
}