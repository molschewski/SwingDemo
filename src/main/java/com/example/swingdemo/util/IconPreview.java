package com.example.swingdemo.util;

import com.example.swingdemo.Viewer;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

import static com.example.swingdemo.util.Utils.createImageIcon;

public class IconPreview {

    Logger log = Logger.getLogger(IconPreview.class.getName());

    private ImageIcon icon;
    private Path imagePath;

    public IconPreview(Path imagePath) {
        this.imagePath = imagePath;
    }

    public ImageIcon getIcon() {

        if (icon == null) {

            int width = Viewer.iconSize;
            int height = Viewer.iconSize;

            ImageIcon tmpIcon;
            try {
                tmpIcon = new ImageIcon(Files.readAllBytes(imagePath));
            } catch (IOException e) {
//                tmpIcon = createImageIcon("images/Open16.gif");
                log.log(Level.ALL, "Could not read " + imagePath.toString());
                throw new RuntimeException(e);
            }

            if (tmpIcon.getImage() == null) {
                tmpIcon = createImageIcon("images/Open16.gif");
                log.log(Level.WARNING, "The file " + imagePath.toString() + " is not an image.");
            }

            int tmpWidth = tmpIcon.getIconWidth();
            int tmpHeight = tmpIcon.getIconHeight();

            if (tmpHeight == 0 || tmpWidth == 0) {
                tmpIcon = createImageIcon("images/Open16.gif");
                log.log(Level.ALL,"The image " + imagePath.toString() + " is missing a dimension.");
            }

            if (tmpHeight > tmpWidth) {
                width = Math.round((tmpWidth * height) / tmpHeight);
            } else if (tmpHeight < tmpWidth) {
                height = Math.round((tmpHeight * width) / tmpWidth);
            } else {
               // do nothing
            }

            icon = new ImageIcon(tmpIcon.getImage()
                    .getScaledInstance(width, height, Image.SCALE_DEFAULT));
        }

//        System.err.println("icon height: " + icon.getIconHeight() + ", icon width: " + icon.getIconWidth());

        return icon;
    }

    public Path getPath() {
        return this.imagePath;
    }
}