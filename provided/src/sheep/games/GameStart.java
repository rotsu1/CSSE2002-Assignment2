package sheep.games;

import sheep.ui.Perform;
import sheep.ui.Prompt;

/**
 * A class that enables the game to start.
 * @param <T> type of game.
 */
public class GameStart<T extends Game> implements Perform {

    /**
     * Reference to the game.
     */
    private T game;

    /**
     * Constructor
     * @param game reference to the game.
     */
    public GameStart(T game) {
        this.game = game;
    }

    /**
     * Starts the game.
     * @param row The currently selected row of the user, or -2 if none
     *            selected.
     * @param column The currently selected column of the user, or -2 if none
     *              selected.
     * @param prompt Provides a mechanism to interact with the user interface
     *               after an interaction, if required.
     */
    @Override
    public void perform(int row, int column, Prompt prompt) {
        game.startGame(row, column);
    }
}
