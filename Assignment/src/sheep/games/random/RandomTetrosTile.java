package sheep.games.random;

import java.util.Random;

/**
 * A class which produces a random tile to be used in the tetros game.
 */
public class RandomTetrosTile implements RandomTile {

    /**
     * A Random instance o produce a random number.
     */
    private Random random;

    /**
     * Constructor
     * @param random random instance to produce a random number.
     */
    public RandomTetrosTile(Random random) {
        this.random = random;
    }

    /**
     * Produces a random number from range 0 to 6.
     * @return a numbr from 0 to 6.
     */
    @Override
    public int pick() {
        return random.nextInt(6);
    }
}
