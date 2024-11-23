package com.example.swingdemo;

import com.example.swingdemo.util.Utils;

import java.io.File;
import javax.swing.filechooser.*;

// ImageFilter.java is used by FileChooserDemo2.java
public class ImageFilter extends FileFilter {

    //Accept all directories and all gif, jpg, tiff, or png files.
    public boolean accept(File file) {

        boolean accepted = false;

        if (file.isDirectory()) {
            accepted = true;
        }

        String extension = Utils.getExtension(file);
        if (accepted != true && extension != null) {
            accepted = Utils.imageExtensions.contains(extension);
        }

        return accepted;
    }

    //The description of this filter
    public String getDescription() {
        return "Just Images";
    }

}