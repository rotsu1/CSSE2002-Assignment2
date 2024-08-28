package sheep.features.files;

import sheep.features.Feature;
import sheep.sheets.Sheet;
import sheep.ui.UI;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringJoiner;

/**
 * Allows users to save the sheet onto a txt file.
 */
public class FileSaving implements Feature {

    /**
     * The sheet that is going to be saved.
     */
    private Sheet sheet;

    /**
     * Constructor.
     * @param sheet that is going to be saved.
     */
    public FileSaving(Sheet sheet) {
        this.sheet = sheet;
    }

    /**
     * Registers saving feature to the ui.
     * @param ui the user interface that the user will interact with.
     */
    public void register(UI ui ) {
        ui.addFeature("save-file", "Save file", getSave());
    }

    /**
     * Returns an instance of SaveFile.
     * @return an instance of SaveFile.
     */
    public SaveFile getSave() {
        return new SaveFile(this);
    }

    /**
     * Saves the details on the sheet to a txt file.
     * @param filename name of file.
     * @requires filename != null && filename.endsWith(".txt").
     * @ensures filename.endsWith(".txt").
     */
    public void save(String filename) {
        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter((filename)))) {
            writer.write(this.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts all data of the sheet into a string.
     * @return a string format of the data of sheet.
     * @ensures first line == row && second line == column && others == content.
     */
    @Override
    public String toString() {
        StringJoiner res = new StringJoiner(System.lineSeparator());

        int rowSize = sheet.getRows();
        int columnSize = sheet.getColumns();
        res.add(Integer.toString(rowSize));
        res.add(Integer.toString(columnSize));

        for (int row = 0; row < rowSize; row++) {
            for (int column = 0; column < columnSize; column++) {
                String value = sheet.valueAt(row, column).getContent();
                if (!value.isEmpty()) {
                    String cell = row + "|" + column + "|" + value;
                    res.add(cell);
                }
            }
        }

        return res.toString();
    }
}
