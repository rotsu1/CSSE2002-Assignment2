package sheep.games.random;

import sheep.sheets.CellLocation;
import sheep.sheets.Sheet;

import java.util.Random;

/**
 * A class which produces a random cell.
 */
public class RandomFreeCell implements RandomCell {

    /**
     * Used to check the range of length and width of sheet.
     */
    private Sheet sheet;

    /**
     * Random instance to produce a random number.
     */
    private Random random;

    /**
     * Constructor
     * @param sheet the sheet to check its range.
     * @param random the instance to create a random number.
     */
    public RandomFreeCell(Sheet sheet, Random random) {
        this.sheet = sheet;
        this.random = random;
    }

    /**
     * Checks if the sheet has no empty cells.
     * @return true if every cell is not empty, otherwise false.
     */
    private boolean isBoardFull() {
        for (int row = 0; row < sheet.getRows(); row++) {
            for (int column = 0; column < sheet.getColumns(); column++) {
                if (sheet.valueAt(row, column).getContent().equals("")) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns a random CellLocation.
     * @return a random CellLocation.
     */
    @Override
    public CellLocation pick() {
        CellLocation location;
        do {
            location = new CellLocation(
                    random.nextInt(sheet.getRows()),
                    random.nextInt(sheet.getColumns()));
        } while (!sheet.valueAt(location.getRow(), location.getColumn()).getContent().equals(""));
        return location;
    }
}
