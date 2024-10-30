package archiver.command;

import archiver.ConsoleHelper;
import archiver.ZipFileManager;
import archiver.exception.PathIsNotFoundException;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ZipCreateCommand extends ZipCommand {
    @Override
    public void execute() throws Exception {
        try {
            ConsoleHelper.writeMessage("Archive creation.");

            ZipFileManager zipFileManager = getZipFileManager();

            ConsoleHelper.writeMessage("Type the full path to to a file or directory to archive.");
            Path sourcePath = Paths.get(ConsoleHelper.readString());
            zipFileManager.createZip(sourcePath);

            ConsoleHelper.writeMessage("Archive created.");

        } catch (PathIsNotFoundException e) {
            ConsoleHelper.writeMessage("A wrong path was defined.");
        }
    }
}
    
    