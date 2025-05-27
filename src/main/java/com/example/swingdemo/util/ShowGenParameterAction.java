package com.example.swingdemo.util;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.example.swingdemo.CivitaiPrompt;
import com.example.swingdemo.Viewer;

import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.drew.metadata.exif.ExifDirectoryBase.TAG_USER_COMMENT;
import static com.example.swingdemo.util.Utils.createImageIcon;

public class ShowGenParameterAction extends AbstractAction {

    Logger log = Logger.getLogger(ShowGenParameterAction.class.getName());

    private Viewer viewer;

    public ShowGenParameterAction(Viewer viewer) {
        super();
        this.viewer = viewer;
        putValue(SHORT_DESCRIPTION, "Show generator parameters");
        putValue(SMALL_ICON, createImageIcon("images/icon_info.png"));
        putValue(LONG_DESCRIPTION, "Show the parameter infos for civitai-files");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (viewer == null) {
            System.err.println("No viewer found, giving up!");
            log.log(Level.SEVERE, "Calling ShowTagsAction without a viewer class.");
            return;
        }

        JToggleButton sourceButton = (JToggleButton) e.getSource();
        boolean selected = sourceButton.getModel().isSelected();

//        System.out.println("Action.selected: " + selected);

        BorderLayout layout = (BorderLayout) viewer.getViewerPanel().getLayout();
        Component layoutComponent = layout.getLayoutComponent(BorderLayout.CENTER);
        viewer.getViewerPanel().remove(layoutComponent);

        // debug
//        JScrollPane tagsListPane = viewer.getTagsListScrollPane();
//        JViewport tagsListViewport = tagsListPane.getViewport();
//        System.err.println(tagsListViewport.toString());
//        for (Component component : tagsListPane.getComponents()) {
//            if (component instanceof JList<?>) {
//                System.err.println(component.toString());
//                JList list = (JList) component;
//                ListModel model = list.getModel();
//                for (int i = 0; i < model.getSize(); ++i) {
//                    System.err.println(model.getElementAt(i));
//                }
//            }
//        }

        if (selected) {
//            resetButtons(sourceButton, viewer);
            viewer.getButtonGroup().setSelected(sourceButton);

            JTextPane imageInfoPane = viewer.getImageInfoPane();
            imageInfoPane.setContentType("text/html");
            JScrollPane imageInfoScrollPane = new JScrollPane(imageInfoPane);
            imageInfoScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            imageInfoScrollPane.setPreferredSize(new Dimension(150, 250));
            imageInfoScrollPane.setMinimumSize(new Dimension(10, 10));

            JSplitPane splitPane = new JSplitPane();
            splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
            splitPane.setRightComponent(imageInfoScrollPane);
            splitPane.setLeftComponent(viewer.getImagePanel());
            viewer.getViewerPanel().add(splitPane, BorderLayout.CENTER);
            viewer.setSplitPane(splitPane);

            IconPreview selectedValue = viewer.getImageList().getSelectedValue();
            if (selectedValue != null) {
                Path path = selectedValue.getPath();
                try (InputStream inputStream = Files.newInputStream(path)) {
                    Metadata metadata = ImageMetadataReader.readMetadata(inputStream);
                    Directory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
                    if (directory == null) {
                        String mesg = "<p>No metadata found in image \""
                                + path.getFileName().toString() + "\"</p>";
                        imageInfoPane.setDocument(Utils.createDoc(mesg));
                        return;
                    }

                    // set default message
                    HTMLDocument response;
                    String mesg = "<p>No user comment section found in the image \""
                            + path.getFileName().toString() + "\"</p>";
                    response = Utils.createDoc(mesg);

                    // search for an existing user comment tag
                    for (Tag tag : directory.getTags()) {
                        if (tag.getTagType() == TAG_USER_COMMENT) {
                            // TODO sanitize and format
                            CivitaiPrompt cps = new CivitaiPrompt();
                            response = cps.parse(tag.toString());
                            break;
                        }
                    }
                    imageInfoPane.setDocument(response);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        } else {
            viewer.getViewerPanel().add(viewer.getImagePanel(), BorderLayout.CENTER);
        }

        viewer.getViewerPanel().revalidate();
    }

}