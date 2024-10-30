package html_editor;

import java.io.*;

public class HTMLFileFilter extends javax.swing.filechooser.FileFilter {

    public boolean accept(File file){

        String path = file.getName();
        String extension = path.substring(path.indexOf("."));
        return  file.isDirectory() ||
         extension.equalsIgnoreCase(".html")
               || extension.equalsIgnoreCase(".htm");
    }

    public String getDescription(){
        return "HTML and HTM files";
    }
}

