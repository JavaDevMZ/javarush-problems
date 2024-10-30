package archiver.command;

import archiver.ConsoleHelper;
import archiver.ZipFileManager;
import archiver.exception.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ZipAddCommand extends ZipCommand{

    public void execute() throws Exception{
      try {
            ConsoleHelper.writeMessage("Adding a file to the archive");

            ZipFileManager zipFileManager = getZipFileManager();

            ConsoleHelper.writeMessage("Input the absolute path of the file");
            Path filePath = Paths.get(ConsoleHelper.readString());
            zipFileManager.addFile(filePath);

        } catch (PathIsNotFoundException e) {
            ConsoleHelper.writeMessage("Wrong file");
        }
    }
}
    
    
    