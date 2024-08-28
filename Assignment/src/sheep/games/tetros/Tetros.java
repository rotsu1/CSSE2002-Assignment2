package sheep.games.tetros;

import sheep.expression.TypeError;
import sheep.expression.basic.Constant;
import sheep.expression.basic.Nothing;
import sheep.features.Feature;
import sheep.games.*;
import sheep.games.random.RandomTile;
import sheep.sheets.CellLocation;
import sheep.sheets.Sheet;
import sheep.ui.*;

import java.util.*;

/**
 * A class which allows users to play Tetros on the sheet.
 */
public class Tetros implements Tick, Feature, Game, MoveCell {

    /**
     * The sheet to be played on.
     */
    private final Sheet sheet;

    /**
     * Determines whether the game is in play or not.
     */
    private boolean started = false;

    /**
     * The type of falling block.
     */
    private int fallingType = 1;

    /**
     * A list which stores the location of falling block.
     */
    private List<CellLocation> contents = new ArrayList<>();

    /**
     * A class which creates a random integer from 0 to 6.
     */
    private final RandomTile randomTile;

    /**
     * Constructor of Tetros.
     * @param sheet The sheet which tetros will be played on.
     * @param randomTile A class which randomly picks an integer from 0 to 6.
     */
    public Tetros(Sheet sheet, RandomTile randomTile) {
        this.sheet = sheet;
        this.randomTile = randomTile;
    }

    /**
     * Registers Tetros on to UI.
     * @param ui A User Interface.
     */
    @Override
    public void register(UI ui) {
        ui.onTick(this);
        ui.addFeature("tetros", "Start Tetros", getStart());
        ui.onKey("a", "Move Left", getMove(-1));
        ui.onKey("d", "Move Right", getMove(1));
        ui.onKey("q", "Rotate Left", getRotate(-1));
        ui.onKey("e", "Rotate Right", getRotate(1));
        ui.onKey("s", "Drop", getMove(0));
    }

    /**
     * Creates a new instance of GameStart.
     * @return GameStart.
     */
    public Perform getStart() {
        return new GameStart<>(this);
    }

    /**
     * Creates a new instance Move.
     * @param direction the direction to shift the falling block.
     * @return Move.
     */
    public Perform getMove(int direction) {
        return new Move<>(direction, this);
    }

    /**
     * Creates a new instance of Rotate.
     * @param direction the direction to rotate the falling block.
     * @return Rotate.
     */
    public Perform getRotate(int direction) {
        return new Rotate(direction, this);
    }

    /**
     * Starts the game by setting started true and dropping a new block.
     */
    @Override
    public void startGame(int row, int column) {
        started = true;
        drop();
    }

    /**
     * Ends the game.
     */
    @Override
    public void endGame() {
        started = false;
    }

    /**
     * Returns started.
     * @return started.
     */
    public boolean getStarted() {
        return started;
    }

    /**
     * Checks if the next state is blocked by other blocks or if it is at the
     * end of the sheet.
     * @param location A cell location where there is an existing block.
     * @return True if the next state is out of bounds or if the block is used
     * by other blocks, otherwise false.
     */
    private boolean isStopper(CellLocation location) {
        if (location.getRow() > sheet.getRows() - 1
                || location.getColumn() > sheet.getColumns() - 1) {
            return true;
        }
        String content = sheet.valueAt(location.getRow(),
                location.getColumn()).getContent();
        return !content.isEmpty();
    }

    /**
     * Checks if the next state is not out of bounds after a key event
     * (shifts and flips).
     * @param locations Cell locations of falling blocks.
     * @return True if each cell is not out of bounds, otherwise false.
     */
    public boolean inBounds(List<CellLocation> locations) {
        for (CellLocation location : locations) {
            if (!sheet.contains(location)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Drops the falling block by 1, every tick.
     * @return True if drop was successful, otherwise false.
     */
    public boolean dropTile() {
        List<CellLocation> newContents = new ArrayList<>();
        for (CellLocation tile : contents) {
            newContents.add(new CellLocation(tile.getRow() + 1,
                    tile.getColumn()));
        }
        clear();
        for (CellLocation newLoc : newContents) {
            if (isStopper(newLoc)) {
                render(contents);
                return false;
            }
        }
        render(newContents);
        contents = newContents;
        return true;
    }

    /**
     * Drops the falling block to the bottom of the sheet.
     */
    public void fullDrop() {
        while (dropTile()) {
            dropTile();
        }
    }

    /**
     * Shifts the block by the given parameter.
     * @param shift Direction of shift.
     */
    @Override
    public void shift(int shift) {
        if (!started) {
            return;
        }
        if (shift == 0) {
            fullDrop();
        }
        List<CellLocation> newContents = new ArrayList<>();
        for (CellLocation tile : contents) {
            newContents.add(new CellLocation(tile.getRow(),
                    tile.getColumn() + shift));
        }
        if (!inBounds(newContents)) {
            return;
        }
        clear();
        render(newContents);
        contents = newContents;
    }

    /**
     * Clears the sheet.
     */
    public void clear() {
        for (CellLocation cell : contents) {
            try {
                sheet.update(cell, new Nothing());
            } catch (TypeError e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Recreates the sheet with the updated content.
     * @param items The updated content.
     */
    public void render(List<CellLocation> items) {
        for (CellLocation cell : items) {
            try {
                sheet.update(cell, new Constant(fallingType));
            } catch (TypeError e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Drops a new block when the previous block hits the ground.
     * @return True if a new block can be spawned and seen on the sheet,
     * otherwise false.
     */
    public boolean drop() {
        contents = new ArrayList<>();
        newPiece();
        for (CellLocation location : contents) {
            if (!sheet.valueAt(location).render().isEmpty()) {
                return true;
            }
        }
        render(contents);

        return false;
    }

    /**
     * Creates a new block and stores the data into contents.
     */
    private void newPiece() {
        int value = randomTile.pick();
        switch (value) {
            case 1 -> {
                contents.add(new CellLocation(0, 0));
                contents.add(new CellLocation(1, 0));
                contents.add(new CellLocation(2, 0));
                contents.add(new CellLocation(2, 1));
                fallingType = 7;
            }
            case 2 -> {
                contents.add(new CellLocation(0, 1));
                contents.add(new CellLocation(1, 1));
                contents.add(new CellLocation(2, 1));
                contents.add(new CellLocation(2, 0));
                fallingType = 5;
            }
            case 3 -> {
                contents.add(new CellLocation(0, 0));
                contents.add(new CellLocation(0, 1));
                contents.add(new CellLocation(0, 2));
                contents.add(new CellLocation(1, 1));
                fallingType = 8;
            }
            case 4 -> {
                contents.add(new CellLocation(0, 0));
                contents.add(new CellLocation(0, 1));
                contents.add(new CellLocation(1, 0));
                contents.add(new CellLocation(1, 1));
                fallingType = 3;
            }
            case 5 -> {
                contents.add(new CellLocation(0, 0));
                contents.add(new CellLocation(1, 0));
                contents.add(new CellLocation(2, 0));
                contents.add(new CellLocation(3, 0));
                fallingType = 6;
            }
            case 6 -> {
                contents.add(new CellLocation(0, 1));
                contents.add(new CellLocation(0, 2));
                contents.add(new CellLocation(1, 1));
                contents.add(new CellLocation(0, 1));
                fallingType = 2;
            }
            case 0 -> {
                contents.add(new CellLocation(0, 0));
                contents.add(new CellLocation(0, 1));
                contents.add(new CellLocation(1, 1));
                contents.add(new CellLocation(1, 2));
                fallingType = 4;
            }
        }
    }

    /**
     * Flips the falling block based on the direction parameter.
     * @param direction The direction to flip the block
     */
    public void flip(int direction) {
        int row = flipRowColumn()[0];
        int column = flipRowColumn()[1];

        List<CellLocation> newCells = new ArrayList<>();

        for (CellLocation location : contents) {
            int newColumn = column + ((row - location.getRow()) * direction);
            int newRow = row + ((column - location.getColumn()) * direction);
            CellLocation replacement = new CellLocation(newRow, newColumn);
            newCells.add(replacement);
        }

        if (!inBounds(newCells)) {
            return;
        }

        clear();
        contents = newCells;
        render(newCells);
    }

    /**
     * Calculate the row and column that is used in flip operation.
     * @return an array with row and column.
     */
    private int[] flipRowColumn() {
        int totalColumn = 0;
        int totalRow = 0;

        for (CellLocation cellLocation : contents) {
            totalColumn += cellLocation.getColumn();
            totalRow += cellLocation.getRow();
        }

        totalColumn /= contents.size();
        totalRow /= contents.size();
        return new int[]{totalRow, totalColumn};
    }

    /**
     * Drop the block every tick, creates a new block when there is no falling
     * block, and ends the game when it can't produce any more blocks.
     * @param prompt Provide a mechanism to interact with the user interface
     *               after a tick occurs, if required.
     * @return True if the game is in play and it hasn't ended, otherwise
     * false.
     */
    @Override
    public boolean onTick(Prompt prompt) {
        if (!started) {
            return false;
        }

        if (!dropTile()) {
            if (drop()) {
                prompt.message("Game Over!");
                started = false;
            }
        }

        clearRow();
        return true;
    }

    /**
     * Clears the row of blocks when the row becomes full.
     */
    private void clearRow() {
        // check if row is full from bottom of sheet.
        for (int row = sheet.getRows() - 1; row >= 0; row--) {
            if (isFull(row)) {
                // for each cell above the full row
                for (int rowX = row; rowX > 0; rowX--) {
                    updateRow(rowX);
                }
                row = row + 1;
            }
        }
    }

    /**
     * Checks if row is full.
     * @param row the row to check.
     * @return True if all of the cells are full, otherwise false.
     */
    private boolean isFull(int row) {
        for (int column = 0; column < sheet.getColumns(); column++) {
            if (sheet.valueAt(row, column).getContent().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Updates a row in the sheet.
     * @param row the row to update.
     */
    private void updateRow(int row) {
        for (int column = 0; column < sheet.getColumns(); column++) {
            try {
                // check that the cell is not the falling block.
                if (!contents.contains(new CellLocation(row - 1, column))) {
                    sheet.update(new CellLocation(row, column),
                            sheet.valueAt(new CellLocation(row - 1, column)));
                }
                // bring down the cells to the row that was full
            } catch (TypeError e) {
                throw new RuntimeException(e);
            }
        }
    }
}