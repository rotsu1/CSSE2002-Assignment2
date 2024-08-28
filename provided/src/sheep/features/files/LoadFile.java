package sheep.features.files;

import sheep.ui.Perform;
import sheep.ui.Prompt;

import java.util.Optional;

/**
 * It performs loading operation.
 */
public class LoadFile implements Perform {

    /**
     * File loading instance that is used to load the file.
     */
    private final FileLoading fileLoading;

    /**
     * Constructor
     * @param fileLoading File loading instance.
     */
    public LoadFile(FileLoading fileLoading) {
        this.fileLoading = fileLoading;
    }

    /**
     * Loads the file to the sheet.
     * @param row The currently selected row of the user, or -2 if none
     *           selected.
     * @param column The currently selected column of the user, or -2 if none
     *              selected.
     * @param prompt asks to input the name of file the user wants to load.
     * @requires prompt != null && filename exists.
     */
    @Override
    public void perform(int row, int column, Prompt prompt) {
        Optional<String> filename = prompt.ask("File Name");
        if (filename.isPresent()) {
            if (!fileLoading.load(filename.get())) {
                prompt.message("Unable to load");
            }
        } else {
            prompt.message("Unable to load");
        }
    }

}
