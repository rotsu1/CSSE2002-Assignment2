package sheep.games;

/**
 * Handles the operation of starting a game.
 */
public interface Game {

    /**
     * To be called whenever one wishes to start a game.
     */
    void startGame(int row, int column);

    /**
     * To be called whenever one wishes to end the game.
     */
    void endGame();
}
