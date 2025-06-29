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

//        System.err.println("ActionEvent e source: " + e.getSource().toString());

        if (viewer == null) {
            System.err.println("No viewer found, giving up!");
            log.log(Level.SEVERE, "Calling FileOpenAction without a viewer class.");
            return;
        }

        // disable all controls
//        this.setEnabled(false);

        // Does the SwingWorker belongs her?
        SwingWorker<List<IconPreview>, IconPreview> worker = new SwingWorker<>() {

            DefaultListModel<IconPreview> model = viewer.getPreviewListModel();

            @Override
            protected List<IconPreview> doInBackground() {
                int returnVal = fileChooser.showOpenDialog(viewer.getViewerPanel());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    icons.clear();
                    File dir = fileChooser.getSelectedFile();
                    FileNameExtensionFilter fnf = getFileNameExtensionFilter();
                    try (Stream<Path> files = Files.list(dir.toPath())) {
                        files
                                .filter(path -> !Files.isDirectory(path))
                                .filter(path -> fnf.accept(path.toFile()))
                                .forEach(path -> publish(new IconPreview(path)));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                return icons;
            }

            @Override
            protected void process(List<IconPreview> chunks) {
                System.err.println("FileOpenAction SwingWorker.process chunk size: " + chunks.size());
                for (int i = 0; i < chunks.size(); i++) {
                    model.addElement(chunks.get(i).getFilledIconPreview());
                    if (i % 10 == 0) {
                        System.err.println("FileOpenAction SwingWorker.process revalidate");
                        System.err.println("FileOpenAction SwingWorker.process model.size(): " + model.getSize());
                        viewer.getPreviewScroll().revalidate();
                    }
                }
                viewer.getPreviewScroll().revalidate();
            }

//            @Override
//            protected void done() {
//                model.removeAllElements();
////                System.err.println("FileOpenAction icons has " + icons.size() + " entries");
//                for (IconPreview icon : icons) {
//                    model.addElement(icon);
//                }
//                FileOpenAction.this.setEnabled(true);
//            }
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