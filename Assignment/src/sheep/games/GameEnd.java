package sheep.games;

import sheep.ui.Perform;
import sheep.ui.Prompt;

/**
 * A class that ends the game.
 */
public class GameEnd<T extends Game> implements Perform {

    /**
     * Reference to the game.
     */
    private T game;

    /**
     * Constructor
     * @param game reference to the game.
     */
    public GameEnd(T game) {
        this.game = game;
    }

    /**
     * Ends the current game it is playing.
     * @param row The currently selected row of the user, or -2 if none
     *            selected.
     * @param column The currently selected column of the user, or -2 if none
     *              selected.
     * @param prompt Provides a mechanism to interact with the user interface
     *               after an interaction, if required.
     */
    @Override
    public void perform(int row, int column, Prompt prompt) {
        game.endGame();
    }
}
