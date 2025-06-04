package com.example.swingdemo.actions;

import com.example.swingdemo.ImagePreview;
import com.example.swingdemo.Viewer;
import com.example.swingdemo.util.IconPreview;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.example.swingdemo.util.Utils.createImageIcon;
import static com.example.swingdemo.util.Utils.readImageFiles;

public class FileOpenAction extends AbstractAction {

    Logger log = Logger.getLogger(FileOpenAction.class.getName());

    private Viewer viewer;
    private JFileChooser fileChooser;
    private List<IconPreview> icons = new ArrayList<>();

    public FileOpenAction(Viewer viewer) {
//        super("Open Directories");
        super();
        this.viewer = viewer;
        createFileChooser();
        putValue(SHORT_DESCRIPTION, "Open directories");
        putValue(SMALL_ICON, createImageIcon("images/icon_files.png"));
        putValue(LONG_DESCRIPTION, "Open a file chooser to select directories");
    }

    @Override
    public void actionPerformed(ActionEvent e) {

//        System.err.println("ActionEvent e source: " + e.getSource().toString());

        if (viewer == null) {
            System.err.println("No viewer found, giving up!");
            log.log(Level.SEVERE, "Calling FileOpenAction without a viewer class.");
            return;
        }

        // disable all controls
        this.setEnabled(false);

        // Does the SwingWorker belongs her?
        SwingWorker<List<IconPreview>, Void> worker = new SwingWorker<>() {

            @Override
            protected List<IconPreview> doInBackground() {
                int returnVal = fileChooser.showOpenDialog(viewer.getViewerPanel());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    icons.clear();
                    File dir = fileChooser.getSelectedFile();
                    try {
                        List<File> files = readImageFiles(dir);
                        for (File file : files) {
                            icons.add(new IconPreview(file.toPath()));
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                return icons;
            }

            @Override
            protected void done() {
                DefaultListModel<IconPreview> model = viewer.getImageListModel();
                model.removeAllElements();
                for (IconPreview icon : icons) {
                    model.addElement(icon);
                }
                FileOpenAction.this.setEnabled(true);
            }
        };

        worker.execute();
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
//        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        return fileChooser;
    }

}