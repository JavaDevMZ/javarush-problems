package archiver;

import archiver.exception.PathIsNotFoundException;
import archiver.exception.WrongZipFileException;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipFileManager {
    // ?????? ???? zip ?????
    private final Path zipFile;

    public ZipFileManager(Path zipFile) {
        this.zipFile = zipFile;
    }

    public void createZip(Path source) throws Exception {
        // ???????????, ?? ????? ??????????, ?? ???????????????? ?????
        
        Path zipDirectory = zipFile.getParent();
        if (Files.notExists(zipDirectory))
            Files.createDirectories(zipDirectory);

        // ????????? zip ?????
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipFile))) {

            if (Files.isDirectory(source)) {
                // ???? ????????? ??????????, ???????? ???????? ?????? ?????? ? ???
                FileManager fileManager = new FileManager(source);
                List<Path> fileNames = fileManager.getFileList();

                
                for (Path fileName : fileNames)
                    addNewZipEntry(zipOutputStream, source, fileName);

            } else if (Files.isRegularFile(source)) {

                // ???? ????????? ??????? ????, ???????? ???????? ???? ?????????? ?? ??'?
                addNewZipEntry(zipOutputStream, source.getParent(), source.getFileName());
            } else {

                // ???? ????????? source ?? ?????????? ? ????, ??????? ???????
                throw new PathIsNotFoundException();
            }
        }
    }

    public void extractAll(Path outputFolder) throws Exception {
        // ???????????, ?? ????? zip ????
        if (!Files.isRegularFile(zipFile)) {
            throw new WrongZipFileException();
        }

        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipFile))) {
            // ????????? ?????????? ?????????, ???? ???? ?? ?????
            if (Files.notExists(outputFolder))
                Files.createDirectories(outputFolder);

            // ??????????? ?? ?????? zip ?????? (?????)
            ZipEntry zipEntry = zipInputStream.getNextEntry();

            while (zipEntry != null) {
                String fileName = zipEntry.getName();
                Path fileFullName = outputFolder.resolve(fileName);

                
                Path parent = fileFullName.getParent();
                if (Files.notExists(parent))
                    Files.createDirectories(parent);

                try (OutputStream outputStream = Files.newOutputStream(fileFullName)) {
                    copyData(zipInputStream, outputStream);
                }
                zipEntry = zipInputStream.getNextEntry();
            }
        }
    }

    public void removeFile(Path path) throws Exception {
        removeFiles(Collections.singletonList(path));
    }

    public void removeFiles(List<Path> pathList) throws Exception {
        // ???????????, ?? ????? zip ????
        if (!Files.isRegularFile(zipFile)) {
            throw new WrongZipFileException();
        }

        
        Path tempZipFile = Files.createTempFile(null, null);

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(tempZipFile))) {
            try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipFile))) {

                ZipEntry zipEntry = zipInputStream.getNextEntry();
                while (zipEntry != null) {

                    Path archivedFile = Paths.get(zipEntry.getName());

                    if (!pathList.contains(archivedFile)) {
                        String fileName = zipEntry.getName();
                        zipOutputStream.putNextEntry(new ZipEntry(fileName));

                        copyData(zipInputStream, zipOutputStream);

                        zipOutputStream.closeEntry();
                        zipInputStream.closeEntry();
                    } else {
                        ConsoleHelper.writeMessage(String.format("???? '%s' ???????? ? ??????.", archivedFile.toString()));
                    }
                    zipEntry = zipInputStream.getNextEntry();
                }
            }
        }

        
        Files.move(tempZipFile, zipFile, StandardCopyOption.REPLACE_EXISTING);
    }

    public List<FileProperties> getFilesList() throws Exception {
        // ???????????, ?? ????? zip ????
        if (!Files.isRegularFile(zipFile)) {
            throw new WrongZipFileException();
        }

        List<FileProperties> files = new ArrayList<>();

        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipFile))) {
            ZipEntry zipEntry = zipInputStream.getNextEntry();

            while (zipEntry != null) {
                // ???? "??????" ?? "??????? ??????" ?? ??????, ???? ??????? ?? ???? ??????????
                
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                copyData(zipInputStream, baos);

                FileProperties file = new FileProperties(zipEntry.getName(), zipEntry.getSize(), zipEntry.getCompressedSize(), zipEntry.getMethod());
                files.add(file);
                zipEntry = zipInputStream.getNextEntry();
            }
        }

        return files;
    }

    private void addNewZipEntry(ZipOutputStream zipOutputStream, Path filePath, Path fileName) throws Exception {
        Path fullPath = filePath.resolve(fileName);
        try (InputStream inputStream = Files.newInputStream(fullPath)) {
            ZipEntry entry = new ZipEntry(fileName.toString());

            zipOutputStream.putNextEntry(entry);

            copyData(inputStream, zipOutputStream);

            zipOutputStream.closeEntry();
        }
    }

    public void addFiles(List<Path> absolutePathList) throws Exception{
         if(!Files.isRegularFile(zipFile)){
                        throw new WrongZipFileException();
                    }
            Path tempZipFile = Files.createTempFile(null, null);
            List<String> archivedFilesNames = new ArrayList<>();

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(tempZipFile))) {
            try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipFile))) {

                ZipEntry zipEntry = zipInputStream.getNextEntry();
                while (zipEntry != null) {

                    Path archivedFile = Paths.get(zipEntry.getName());


                        String fileName = zipEntry.getName();
                        zipOutputStream.putNextEntry(new ZipEntry(fileName));

                        copyData(zipInputStream, zipOutputStream);
                         archivedFilesNames.add(fileName);
                        zipOutputStream.closeEntry();
                        zipInputStream.closeEntry();


                    zipEntry = zipInputStream.getNextEntry();
                }
               for(Path file : absolutePathList){
                    if(!Files.isRegularFile(file)){
                                    throw new PathIsNotFoundException();
                                }
                    if(!archivedFilesNames.contains(file.getFileName().toString())){
             zipOutputStream.putNextEntry(new ZipEntry(file.getFileName().toString()));
                     try(InputStream newFileIS = Files.newInputStream(file)){
                copyData(newFileIS, zipOutputStream);
                                    }
                                      }else{
                        ConsoleHelper.writeMessage(String.format("???? %s ??? ? ? ??????", file.getFileName().toString()));
                                    }
                                       }
            }
        }

        
        Files.move(tempZipFile, zipFile, StandardCopyOption.REPLACE_EXISTING);
                }

    public void addFile(Path absolutePath) throws Exception{
        addFiles(Collections.singletonList(absolutePath));
                    }

    private void copyData(InputStream in, OutputStream out) throws Exception {
        byte[] buffer = new byte[8 * 1024];
        int len;
        while ((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
    }
}
                
                
              