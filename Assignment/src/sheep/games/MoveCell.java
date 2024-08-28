package sheep.games;

/**
 * Moves the cell location by the given direction.
 */
public interface MoveCell {

    /**
     * Shifts the cell by the direction give.
     * @param direction the direction to shift the cell.
     */
    void shift(int direction);
}
