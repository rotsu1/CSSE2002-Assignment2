package sheep.features.files;

import sheep.expression.TypeError;
import sheep.expression.basic.Constant;
import sheep.features.Feature;
import sheep.sheets.CellLocation;
import sheep.sheets.Sheet;
import sheep.ui.UI;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Loads the file to the sheet.
 */
public class FileLoading implements Feature {

    /**
     * The sheet to load the file.
     */
    private final Sheet sheet;

    /**
     * Constructor
     * @param sheet the sheet to load the file.
     * @requires sheet != null.
     */
    public FileLoading(Sheet sheet) {
        this.sheet = sheet;
    }

    /**
     * registers Load file to the feature.
     * @param ui User interface which the user will interact with.
     */
    public void register(UI ui) {
        ui.addFeature("load-file", "Load file", getLoad());
    }

    /**
     * Returns an instance of LoadFile.
     * @return instance of LoadFile.
     */
    public LoadFile getLoad() {
        return new LoadFile(this);
    }

    /**
     * Tries to update the sheet by the given file.
     * @param filename the file that contains the contents.
     * @return true if load operation was successful, otherwise false.
     * @requires filename != null.
     */
    public boolean load(String filename) {
        sheet.clear();
        try (BufferedReader reader =
                     new BufferedReader(new FileReader(filename))) {
            if (!updateDimension(reader)) {
                return false;
            }
            if (!updateSheet(reader)) {
                return false;
            }
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    /**
     * Updates the dimension of sheet with the given length of row and column
     * from the file.
     * @param reader A reader from which to read data from a file.
     * @return true if the update was successful, otherwise false.
     * @requires reader != null && (first line and second line of file is an
     *          integer).
     * @ensures sheet.getRow() == first line of file && sheet.getColumn() == second
     *          line of file.
     */
    public boolean updateDimension(BufferedReader reader) {
        int row;
        int column;
        try {
            row = Integer.parseInt(reader.readLine());
            column = Integer.parseInt(reader.readLine());
        } catch (NullPointerException | IOException e) {
            return false;
        }
        sheet.updateDimensions(row, column);
        return true;
    }

    /**
     * Updates the sheet with the given data from the file.
     * @param reader A reader from which to read data from a file.
     * @return true if the update was successful, otherwise false.
     * @requires line.split("\\|").length == 3 && line[0]split("\\|") is
     *          integer && line[1].split("\\|") is integer.
     * @ensures Sheet is updated with values which was in the file.
     */
    public boolean updateSheet(BufferedReader reader) {
        int row;
        int column;
        int number;
        try {
            String cells = reader.readLine();
            while (cells != null) {
                String[] content = cells.split("\\|");
                if (content.length != 3) {
                    return false;
                }
                try {
                    row = Integer.parseInt(content[0]);
                    column = Integer.parseInt(content[1]);
                    number = Integer.parseInt(content[2]);
                    CellLocation cell = new CellLocation(row, column);
                    Constant value = new Constant(number);
                    sheet.update(cell, value);
                } catch (NumberFormatException | TypeError e) {
                    return false;
                }
                cells = reader.readLine();
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
