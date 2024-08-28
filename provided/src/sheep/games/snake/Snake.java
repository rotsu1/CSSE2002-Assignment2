package sheep.games.snake;

import sheep.expression.TypeError;
import sheep.expression.basic.Constant;
import sheep.expression.basic.Nothing;
import sheep.features.Feature;
import sheep.games.Game;
import sheep.games.GameStart;
import sheep.games.MoveCell;
import sheep.games.random.RandomCell;
import sheep.games.Move;
import sheep.sheets.CellLocation;
import sheep.sheets.Sheet;
import sheep.ui.Perform;
import sheep.ui.Prompt;
import sheep.ui.Tick;
import sheep.ui.UI;

import java.util.ArrayList;
import java.util.List;

/**
 * Snake game.
 */
public class Snake implements Tick, Feature, Game, MoveCell {

    /**
     * Game has started if true, otherwise false.
     */
    private boolean started = false;

    /**
     * List of cells which represent a snake.
     */
    private List<CellLocation> snake = new ArrayList<>();

    /**
     * A list of cells which represents foods.
     */
    private List<CellLocation> food = new ArrayList<>();

    /**
     * The sheet used to play the snake game.
     */
    private final Sheet sheet;

    /**
     * The direction of the snake moving.
     */
    private int direction = -2;

    /**
     * The cell which represents the head of snake.
     */
    private CellLocation snakeHead;

    /**
     * True if snake ate the food on the tick, otherwise false.
     */
    private boolean ate = false;

    /**
     * Used to randomly produce food after the snake consumed food.
     */
    public final RandomCell randomCell;

    /**
     * Constructor.
     * @param sheet the sheet used to play the snake game.
     * @param randomCell used to randomly produce food.
     */
    public Snake(Sheet sheet, RandomCell randomCell) {
        this.sheet = sheet;
        this.randomCell = randomCell;
    }

    /**
     * Registers the snake game to the UI.
     * @param ui the user interface which the user will be interacting with.
     */
    @Override
    public void register(UI ui) {
        ui.onTick(this);
        ui.addFeature("snake", "Snake game", getStart());
        ui.onKey("a", "Move Left", getMove(-1));
        ui.onKey("d", "Move Right", getMove(1));
        ui.onKey("w", "Move Up", getMove(2));
        ui.onKey("s", "Move Down", getMove(-2));
    }

    /**
     * Starts the game.
     * @param row initial row position of the snake.
     * @param column initial column position of the snake.
     */
    @Override
    public void startGame(int row, int column) {
        started = true;
        getCurrentSheet();
        if (snake.isEmpty()) {
            try {
                sheet.update(new CellLocation(row, column), new Constant(1));
            } catch (TypeError e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Ends the game.
     */
    @Override
    public void endGame() {
        started = false;
    }

    /**
     * Moves or grows the snake on every tick.
     * @param prompt Provide a mechanism to interact with the user interface
     *               after a tick occurs, if required.
     * @return true if the game is running, otherwise false.
     */
    @Override
    public boolean onTick(Prompt prompt) {
        if (!started) {
            return false;
        }
        getCurrentSheet();
        if (ate) {
            if (!grow()) {
                gameOver(prompt);
            }
            return true;
        }
        if (!move()) {
            gameOver(prompt);
        }
        return true;
    }

    /**
     * Changes the direction of snake.
     * @param direction the direction to move the snake.
     * @requires direction == -1 || direction == 1 || direction == -2 ||
     *          direction == 2.
     */
    @Override
    public void shift(int direction) {
        this.direction = direction;
    }

    /**
     * Ends the game by giving the game over message to the user.
     * @param prompt used to show game over message.
     */
    public void gameOver(Prompt prompt) {
        prompt.message("Game Over!");
        started = false;
    }

    /**
     * Checks if the snake is inside the sheet.
     * @param locations the cell location of the snake.
     * @return true if the snake is inside the sheet, otherwise false.
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
     * Moves the snake.
     * @return true if the snake moved and it is inside the sheet, otherwise
     * false.
     */
    public boolean move() {
        if (!started) {
            return false;
        }
        clear(snake);
        clear(food);

        snakeHead = snake.getLast();
        int row = snakeHead.getRow();
        int column = snakeHead.getColumn();
        CellLocation newHead = new CellLocation(row + getRowShift(),
                column + getColumnShift());

        snake.add(newHead);
        snake.removeFirst();

        if (food.contains(newHead)) {
            ate = true;
            newFood();
            food.remove(newHead);
        }

        if (!inBounds(snake)) {
            return false;
        }

        render(food, 2);
        render(snake, 1);
        return true;
    }

    /**
     * Returns the direction of the snake.
     * @return direction if direction == -1 || direction == 1, otherwise 0.
     * @ensures getRowShift() == 0 if direction == -1 || direction == 1.
     */
    private int getColumnShift() {
        if (direction == -1 || direction == 1) {
            return direction;
        }
        return 0;
    }

    /**
     * Returns the direction of the snake.
     * @return 1 if direction == -2, -1 if direction == 2, otherwise 0.
     * @ensures getColumnShift() == 0 if direction == -2 || direction == 1.
     */
    private int getRowShift() {
        if (direction == -2) {
            return 1;
        } else if(direction == 2) {
            return -1;
        }
        return 0;
    }

    /**
     * Grows the snake by one cell.
     * @return true if the snake grows.
     * @ensures \old(snake).size() < snake.size().
     */
    public boolean grow() {
        clear(snake);
        clear(food);
        snakeHead = snake.getLast();
        int row = snakeHead.getRow() + getRowShift();
        int column = snakeHead.getColumn() + getColumnShift();

        CellLocation newHead = new CellLocation(row, column);
        snake.add(newHead);

        if (!inBounds(snake)) {
            snake.removeLast();
            if (row == -1) {
                row = sheet.getRows() - 1;
            } else if (row == sheet.getRows()) {
                row = 0;
            } else if (column == -1) {
                column = sheet.getColumns() - 1;
            } else if (column == sheet.getColumns()) {
                column = 0;
            }
            newHead = new CellLocation(row, column);
            snake.add(newHead);
        }

        food.remove(snakeHead);
        if (ate) {
            newFood();
        }
        render(food, 2);
        render(snake, 1);
        ate = food.contains(newHead);
        return true;
    }

    /**
     * Produces a new food onto the sheet.
     */
    private void newFood() {
        CellLocation newFood = randomCell.pick();
        if (snake.contains(newFood)) {
            return;
        }
        food.add(newFood);
    }

    /**
     * Clears the sheet.
     */
    public void clear(List<CellLocation> items) {
        for (CellLocation cell : items) {
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
    public void render(List<CellLocation> items, int object) {
        for (CellLocation cell : items) {
            try {
                sheet.update(cell, new Constant(object));
            } catch (TypeError e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Returns an instance of GameStart.
     * @return GameStart.
     */
    public Perform getStart() {
        return new GameStart<>(this);
    }

    /**
     * Returns an instance of Move
     * @param direction the direction of the snake.
     *                  By default, it should be -2.
     * @return Move.
     */
    public Perform getMove(int direction) {
        return new Move<>(direction, this);
    }

    /**
     * Gets the data of the current sheet.
     */
    public void getCurrentSheet() {
        for (int row = 0; row < sheet.getRows(); row ++) {
            for (int column = 0; column < sheet.getColumns(); column++) {
                String content = sheet.valueAt(row, column).getContent();
                CellLocation location = new CellLocation(row, column);
                if (!snake.contains(location) && !food.contains(location)) {
                    if (content.equals("1")) {
                        snake.add(new CellLocation(row, column));
                    } else if (content.equals("2")) {
                        food.add(new CellLocation(row, column));
                    }
                }
            }
        }
    }
}
