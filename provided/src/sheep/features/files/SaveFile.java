package sheep.features.files;

import sheep.ui.Perform;
import sheep.ui.Prompt;

import java.util.Optional;

/**
 * Performs saving operation.
 */
public class SaveFile implements Perform {

    /**
     * FileSaving instance which is used to save data.
     */
    private FileSaving fileSave;

    /**
     * Saves the data into a .txt format.
     * It will prompt the user for a file name.
     * @param fileSave used to save data.
     */
    public SaveFile(FileSaving fileSave) {
        this.fileSave = fileSave;
    }
    @Override
    public void perform(int row, int column, Prompt prompt) {
        Optional<String> filename = prompt.ask("File Name");
        if (filename.isPresent()) {
            fileSave.save(filename.get());
        } else {
            prompt.message("Unable to save");
        }
    }

}
