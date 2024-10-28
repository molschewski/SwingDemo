package com.example.swingdemo;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import javax.swing.ImageIcon;

/* Utils.java is used by FileChooserDemo2.java. */
public class Utils {

    public final static String jpeg = "jpeg";
    public final static String jpg = "jpg";
    public final static String gif = "gif";
    public final static String tiff = "tiff";
    public final static String tif = "tif";
    public final static String png = "png";

    public final static HashSet<String> imageExtensions = new HashSet<>(
            Arrays.asList("gif", "jpg", "jpeg", "png", "tif", "tiff"));

    // Get the extension of a file.
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

    // Returns an ImageIcon, or null if the path was invalid.
    protected static ImageIcon createImageIcon(String path) {

        URL imgURL = Thread.currentThread().getContextClassLoader().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}