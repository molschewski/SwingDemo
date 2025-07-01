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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static com.example.swingdemo.util.Utils.*;

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

        if (viewer == null) {
            System.err.println("No viewer found, giving up!");
            log.log(Level.SEVERE, "Calling FileOpenAction without a viewer class.");
            return;
        }

        int returnVal = fileChooser.showOpenDialog(viewer.getViewerPanel());

        // Does the SwingWorker belongs her?
        SwingWorker<Integer, IconPreview> worker = new SwingWorker<>() {

            DefaultListModel<IconPreview> model = viewer.getPreviewListModel();

            @Override
            protected Integer doInBackground() {
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    model.removeAllElements();
                    icons.clear();
                    File dir = fileChooser.getSelectedFile();
                    FileNameExtensionFilter fnf = getFileNameExtensionFilter();
                    try (Stream<Path> files = Files.list(dir.toPath())) {
                        files
                                .filter(path -> !Files.isDirectory(path))
                                .filter(path -> fnf.accept(path.toFile()))
                                .forEach(path -> publish(new IconPreview(path).getFilledIconPreview()));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                return Integer.valueOf(1);
            }

            @Override
            protected void process(List<IconPreview> chunks) {
                for (IconPreview iconPreview : chunks) {
                    model.addElement(iconPreview);
                }
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