package sheep.games.random;

import sheep.sheets.CellLocation;

/**
 * An interface which produces a random CellLocation.
 */
public interface RandomCell {

    /**
     * Returns a CellLocation with random row and random column.
     * It will be used to produce something in a cell at a random location.
     * @return A CellLocation with random rows and columns.
     */
    CellLocation pick();
}
