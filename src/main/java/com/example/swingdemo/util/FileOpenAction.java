package com.example.swingdemo.util;

import com.example.swingdemo.ImagePreview;
import com.example.swingdemo.Viewer;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.example.swingdemo.util.Utils.setIconsForModel;

public class FileOpenAction extends AbstractAction {

    Logger log = Logger.getLogger(FileOpenAction.class.getName());

    private Viewer viewer;
    private JFileChooser fileChooser;

    public FileOpenAction(Viewer viewer) {
        super("Open Files");
        this.viewer = viewer;
        createFileChooser();
        putValue(SHORT_DESCRIPTION, "Open files");
    }

    @Override
    public void actionPerformed(ActionEvent e) {

//        //debug
//        System.err.println("ActionEvent e source: " + e.getSource().toString());

        if (viewer == null) {
            System.err.println("No viewer found, giving up!");
            log.log(Level.SEVERE, "Calling FileOpenAction without a viewer class.");
            return;
        }

        int returnVal = fileChooser.showOpenDialog(viewer.getViewerGUI());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File dir = fileChooser.getSelectedFile();
            try {
                setIconsForModel(viewer.getModel(), dir);
                if (viewer.getModel().isEmpty()) {
                    return;
                }

                Path path = viewer.getModel().firstElement().getPath();
                byte[] imgBytes = Files.readAllBytes(path);
                viewer.getPicture().setIcon(new ImageIcon(imgBytes));
                viewer.getPicture().revalidate();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private JFileChooser createFileChooser() {
        fileChooser = new JFileChooser();
        String[] imageTypes = ImageIO.getReaderFileSuffixes();
        FileNameExtensionFilter fnf = new FileNameExtensionFilter("Images", imageTypes);
        fileChooser.setFileFilter(fnf);
//            File userHome = new File(System.getProperty("user.home"));
//            fileChooser.setSelectedFile(userHome);

        // add the preview pane for the file chooser and set the selection mode
        fileChooser.setAccessory(new ImagePreview(fileChooser));
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//            fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        return fileChooser;
    }

}