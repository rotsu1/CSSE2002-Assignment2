package sheep.games.life;

import sheep.expression.TypeError;
import sheep.expression.basic.Constant;
import sheep.expression.basic.Nothing;
import sheep.features.Feature;
import sheep.games.Game;
import sheep.games.GameStart;
import sheep.games.GameEnd;
import sheep.sheets.CellLocation;
import sheep.sheets.Sheet;
import sheep.ui.Perform;
import sheep.ui.Prompt;
import sheep.ui.Tick;
import sheep.ui.UI;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Game of Life class.
 */
public class Life implements Tick, Feature, Game {

    /**
     * The sheet to be played on.
     */
    private final Sheet sheet;

    /**
     * Determines whether the game is in play or not.
     */
    private boolean started = false;

    private List<CellLocation> contents = new ArrayList<>();

    /**
     * Constructor
     * @param sheet the sheet the display the game.
     */
    public Life(Sheet sheet) {
        this.sheet = sheet;
    }

    /**
     * Creates a new instance of GameStart.
     * @return GameStart.
     */
    public Perform getStart() {
        return new GameStart<>(this);
    }

    /**
     * Creates a new instance of GameEnd.
     * @return GameEnd.
     */
    public Perform getEnd() {
        return new GameEnd<>(this);
    }

    /**
     * Registers GoL to the feature so users can run GoL.
     * @param ui the user interface which the user will be interacting with.
     */
    @Override
    public void register(UI ui) {
        ui.onTick(this);
        ui.addFeature("gol-start", "Start GoL", getStart());
        ui.addFeature("gol-end", "End GoL", getEnd());
    }

    /**
     * Starts the game
     * @ensures started == true.
     */
    @Override
    public void startGame(int row, int column) {
        started = true;
        getCurrentGrid();
        updateGrid();
    }

    /**
     * Gets the current grid details.
     */
    private void getCurrentGrid() {
        for (int row = 0; row < sheet.getRows(); row++) {
            for (int column = 0; column < sheet.getColumns(); column++) {
                String isLive = sheet.valueAt(row, column).getContent();
                if (Objects.equals(isLive, "1")) {
                    contents.add(new CellLocation(row, column));
                }
            }
        }
    }

    /**
     * Stops running GoL.
     */
    @Override
    public void endGame() {
        started = false;
    }

    /**
     * Updates the grid every tick.
     * @param prompt Provide a mechanism to interact with the user interface
     *               after a tick occurs, if required.
     * @return true if the game is running, otherwise false.
     */
    @Override
    public boolean onTick(Prompt prompt) {
        if (!started) {
            return false;
        } else {
            getCurrentGrid();
            updateGrid();
            return true;
        }
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
                sheet.update(cell, new Constant(1));
            } catch (TypeError e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Calculates the number of alive neighbours.
     * @param neighbours the position of neighbours.
     * @return number of alive neighbours.
     */
    private int neighbourCalculator(Integer[][] neighbours) {
        int total = 0;

        for (Integer[] neighbour : neighbours) {
            String live = sheet.valueAt(neighbour[0], neighbour[1])
                    .getContent();
            if (Objects.equals(live, "1")) {
                total += 1;
            }
        }
        return total;
    }

    /**
     * Returns the next state by applying GoL rule.
     * @param current the current state.
     * @param neighbours number of alive neighbours.
     * @return true if alive in the next state, otherwise false.
     * @required neighbours >= 0.
     */
    private boolean gol(String current, int neighbours) {
        return neighbours == 3
                || (neighbours == 2 && Objects.equals(current, "1"));
    }

    /**
     * Checks each grid to determine if it will be alive in the next state
     */
    public void updateGrid() {
        int bottomRow = sheet.getRows() - 1;
        int rightColumn = sheet.getColumns() - 1;
        List<CellLocation> newContent = new ArrayList<>();

        for (int row = 0; row < sheet.getRows(); row++) {
            for (int column = 0; column < sheet.getColumns(); column++) {
                boolean nextGen;
                if (row == 0 && column == 0) {
                    nextGen = isLiveTopLeft();
                } else if (row == 0 && column == rightColumn) {
                    nextGen = isLiveTopRight();
                } else if (row == bottomRow && column == 0) {
                    nextGen = isLiveBottomLeft();
                } else if (row == bottomRow && column == rightColumn) {
                    nextGen = isLiveBottomRight();
                } else if (row == 0) {
                    nextGen = isLiveTop(row, column);
                } else if (column == 0) {
                    nextGen = isLiveLeft(row, column);
                } else if (row == bottomRow) {
                    nextGen = isLiveBottom(row, column);
                } else if (column == rightColumn) {
                    nextGen = isLiveRight(row, column);
                } else {
                    nextGen = isLiveOther(row, column);
                }
                if (nextGen) {
                    newContent.add(new CellLocation(row, column));
                }
            }
        }

        clear();
        contents = newContent;
        render(contents);
    }

    /**
     * Returns the next state by applying GoL rule for cell in top left corner.
     * @return true if alive in the next state, otherwise false.
     */
    private boolean isLiveTopLeft() {
        String current = sheet.valueAt(0, 0).getContent();
        Integer[][] neighbours = { {0, 1}, {1, 0}, {1, 1} };
        int total = neighbourCalculator(neighbours);
        return gol(current, total);
    }

    /**
     * Returns the next state by applying GoL rule for cell in top right
     * corner.
     * @return true if alive in the next state, otherwise false.
     */
    private boolean isLiveTopRight() {
        int right = sheet.getColumns() - 1;
        String current = sheet.valueAt(0, right).getContent();
        Integer[][] neighbours = { {0, right - 1}, {1, right}, {1, right - 1} };
        int total = neighbourCalculator(neighbours);
        return gol(current, total);
    }

    /**
     * Returns the next state by applying GoL rule for cell in bottom right
     * corner.
     * @return true if alive in the next state, otherwise false.
     */
    private boolean isLiveBottomRight() {
        int bottom = sheet.getRows() - 1;
        int right = sheet.getColumns() - 1;
        String current = sheet.valueAt(bottom, right).getContent();
        Integer[][] neighbours = { {bottom, right - 1}, {bottom - 1, right},
                                   {bottom - 1, right - 1} };
        int total = neighbourCalculator(neighbours);
        return gol(current, total);
    }

    /**
     * Returns the next state by applying GoL rule for cell in bottom left
     * corner.
     * @return true if alive in the next state, otherwise false.
     */
    private boolean isLiveBottomLeft() {
        int bottom = sheet.getRows() - 1;
        String current = sheet.valueAt(bottom, 0).getContent();
        Integer[][] neighbours = { {bottom, 1}, {bottom - 1, 0}, {bottom - 1, 1} };
        int total = neighbourCalculator(neighbours);
        return gol(current, total);
    }

    /**
     * Returns the next state by applying GoL rule for cell in top edge
     * excluding corners.
     * @param row the row of the current state it is looking at.
     * @param column the column of the current state it is looking at.
     * @return true if alive in the next state, otherwise false.
     */
    private boolean isLiveTop(int row, int column) {
        String current = sheet.valueAt(row, column).getContent();
        Integer[][] neighbours = { {0, column - 1}, {0, column + 1}, {1, column - 1}, {1, column},
                                   {1, column + 1} };
        int total = neighbourCalculator(neighbours);
        return gol(current, total);
    }

    /**
     * Returns the next state by applying GoL rule for cell in left edge
     * excluding corners.
     * @param row the row of the current state it is looking at.
     * @param column the column of the current state it is looking at.
     * @return true if alive in the next state, otherwise false.
     */
    private boolean isLiveLeft(int row, int column) {
        String current = sheet.valueAt(row, column).getContent();
        Integer[][] neighbours = { {row - 1, 0}, {row - 1, 1}, {row, 1}, {row + 1, 0},
                                   {row + 1, 1} };
        int total = neighbourCalculator(neighbours);
        return gol(current, total);
    }

    /**
     * Returns the next state by applying GoL rule for cell in right edge
     * excluding corners.
     * @param row the row of the current state it is looking at.
     * @param column the column of the current state it is looking at.
     * @return true if alive in the next state, otherwise false.
     */
    private boolean isLiveRight(int row, int column) {
        String current = sheet.valueAt(row, column).getContent();
        Integer[][] neighbours = { {row - 1, column}, {row - 1, column - 1}, {row, column - 1},
                                   {row + 1, column}, {row + 1, column - 1} };
        int total = neighbourCalculator(neighbours);
        return gol(current, total);
    }

    /**
     * Returns the next state by applying GoL rule for cell in bottom edge
     * excluding corners.
     * @param row the row of the current state it is looking at.
     * @param column the column of the current state it is looking at.
     * @return true if alive in the next state, otherwise false.
     */
    private boolean isLiveBottom(int row, int column) {
        String current = sheet.valueAt(row, column).getContent();
        Integer[][] neighbours = { {row, column - 1}, {row, column + 1}, {row - 1, column - 1},
                                   {row - 1, column}, {row - 1, column + 1} };
        int total = neighbourCalculator(neighbours);
        return gol(current, total);
    }

    /**
     * Returns the next state by applying GoL rule for cell that are not
     * corners or edges of the sheet.
     * @param row the row of the current state it is looking at.
     * @param column the column of the current state it is looking at.
     * @return true if alive in the next state, otherwise false.
     */
    private boolean isLiveOther(int row, int column) {
        String current = sheet.valueAt(row, column).getContent();
        Integer[][] neighbours = { {row - 1, column - 1}, {row - 1, column}, {row - 1, column + 1},
                                   {row, column - 1}, {row, column + 1}, {row + 1, column - 1},
                                   {row + 1, column}, {row + 1, column + 1} };
        int total = neighbourCalculator(neighbours);
        return gol(current, total);
    }
}
