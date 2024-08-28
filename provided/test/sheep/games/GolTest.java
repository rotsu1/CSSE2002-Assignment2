package sheep.games;

import org.junit.Before;
import org.junit.Test;
import sheep.expression.CoreFactory;
import sheep.expression.ExpressionFactory;
import sheep.expression.TypeError;
import sheep.expression.basic.Constant;
import sheep.features.Feature;
import sheep.games.life.Life;
import sheep.parsing.SimpleParser;
import sheep.sheets.CellLocation;
import sheep.sheets.Sheet;
import sheep.sheets.SheetBuilder;

import java.util.Map;
import java.util.StringJoiner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GolTest {

    private Sheet sheet;
    private MockUI ui;

    @Before
    public void setup() {
        setupSheet(10, 5);
    }

    private void setupSheet(int rows, int columns) {
        ExpressionFactory factory = new CoreFactory();
        this.sheet = new SheetBuilder(new SimpleParser(factory), factory.createEmpty())
                .empty(rows, columns);
        ui = new MockUI(sheet, sheet);

        Feature game = new Life(sheet);
        game.register(ui);
    }

    private String expectedSheet(Map<CellLocation, String> exceptions) {
        StringJoiner builder = new StringJoiner(System.lineSeparator());
        for (int row = 0; row < sheet.getRows(); row++) {
            StringJoiner rowString = new StringJoiner("|");
            for (int column = 0; column < sheet.getColumns(); column++) {
                String expected = exceptions.getOrDefault(new CellLocation(row, column), "");
                rowString.add(expected);
            }
            builder.add(rowString.toString());
        }
        return builder.toString();
    }

    private void assertEmptyExcept(Map<CellLocation, String> exceptions) {
        for (CellLocation location : exceptions.keySet()) {
            assertTrue(sheet.contains(location));
        }
        String expectedSheet = expectedSheet(exceptions);
        String actualSheet = sheet.encode();
        for (int row = 0; row < sheet.getRows(); row++) {
            for (int column = 0; column < sheet.getColumns(); column++) {
                CellLocation location = new CellLocation(row, column);
                String expected = exceptions.getOrDefault(location, "");
                String actual = sheet.valueAt(row, column).getContent();
                assertEquals(
                        "Difference value at (" + row + ", " + column + "): Expected "
                                + expected + " Got " + actual
                                + System.lineSeparator()
                                + "Expected" + System.lineSeparator()
                                + expectedSheet + System.lineSeparator()
                                + "Got" + System.lineSeparator()
                                + actualSheet,
                        expected, actual
                );
            }
        }
    }

    @Test
    public void playGame() throws TypeError {
        assertEmptyExcept(Map.of());
        this.sheet.update(new CellLocation(0,0), new Constant(1));
        ui.simulateFeature("gol-start", 1, 1);
        ui.simulateTick();
        assertEmptyExcept(Map.of(
                new CellLocation(0, 0), ""
        ));
    }

}
