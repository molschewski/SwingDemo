package com.example.swingdemo;

import com.example.swingdemo.util.FileOpenAction;
import com.example.swingdemo.util.IconPreview;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.example.swingdemo.util.Utils.*;

@Component
public class Viewer {

    public static int iconSize = 128;

    private JPanel viewerGUI;
    private ScrollablePicture picture;
    private DefaultListModel<IconPreview> model;
    private JList<IconPreview> imageList;
    private Action fileOpenAction;
    private JMenuBar menuBar;

    public Viewer() {

    }

    @Bean
    public static void createViewer() {
        JFrame frame = new JFrame("Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Viewer viewer = new Viewer();
        Action fileOpAction = new FileOpenAction(viewer);
        viewer.setFileOpenAction(fileOpAction);

        //Add content to the window
        try {
            viewer.menuBar = viewer.createMenuBar();
            frame.setJMenuBar(viewer.menuBar);
            viewer.viewerGUI = viewer.createViewerGUI();
            frame.add(viewer.viewerGUI);
        } catch (Exception e) {
            System.err.println(e);
            throw new RuntimeException(e);
        }

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    private JMenuBar createMenuBar() {

        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("Images");
        menuBar.add(menu);

        JMenuItem menuItem = new JMenuItem(fileOpenAction);
        menu.add(menuItem);

        return menuBar;
    }

    public JPanel createViewerGUI() throws Exception {

        JPanel panel = new JPanel(new GridBagLayout());

        //////////
        // buttons
        GridBagConstraints gbcOpenButton = new GridBagConstraints();
        JButton openButton = new JButton(fileOpenAction);
        openButton.setIcon(createImageIcon("images/Open16.gif"));

//        PropertyChangeListener[] propertyChangeListeners = openButton.getPropertyChangeListeners();
//        for (PropertyChangeListener pcl : propertyChangeListeners) {
//            System.err.println("PropertyChangeListener: " + pcl.toString());
//        }
//        Arrays.stream(openButton.getChangeListeners()).toList();

        // debug
        openButton.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("enabled")) {
                    System.err.println("openButton Event: " + evt.toString());
                }
//                    boolean isEnabled = (Boolean)evt.getNewValue();
//                    for (AbstractButton button : buttons) {
//                        button.setEnabled(isEnabled);
//                    }
//                }
            }
        });

        ////////
        // image
        GridBagConstraints gbcPictureScrollPane = new GridBagConstraints();
        picture = new ScrollablePicture(1);
        JScrollPane pictureScrollPane = new JScrollPane(picture);
        pictureScrollPane.setPreferredSize(new Dimension(600, 600));
        pictureScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        pictureScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        pictureScrollPane.setViewportBorder(BorderFactory.createLineBorder(Color.black));

        ///////////////////////
        // image scroll preview
        GridBagConstraints gbcImageScroll = new GridBagConstraints();
        model = new DefaultListModel<>();
        imageList = new JList<>(model);
        imageList.setCellRenderer(new IconCellRenderer2());
        ListSelectionListener listener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                IconPreview selectedValue = imageList.getSelectedValue();
                try {
                    if (selectedValue == null) {
                        return;
                    }
                    Path path = selectedValue.getPath();
                    if (path == null) {
                        // do nothing till I have a nice placeholder
                        return;
                    }
                    byte[] imgBytes = Files.readAllBytes(path);
                    picture.setIcon(new ImageIcon(imgBytes));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        imageList.addListSelectionListener(listener);

        imageList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        imageList.setVisibleRowCount(1);

        JScrollPane imageScroll = new JScrollPane(
                imageList,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        imageScroll.setPreferredSize(new Dimension(600, 100));

        gbcOpenButton.gridx = 0;
        gbcOpenButton.gridy = 0;
        gbcOpenButton.anchor = GridBagConstraints.LINE_START;
        panel.add(openButton, gbcOpenButton);

        gbcPictureScrollPane.weightx = 0.5;
        gbcPictureScrollPane.weighty = 0.5;
        gbcPictureScrollPane.gridx = 0;
        gbcPictureScrollPane.gridy = 1;
        panel.add(pictureScrollPane, gbcPictureScrollPane);

        gbcImageScroll.weightx = 1.0;
        gbcImageScroll.weighty = 1.0;
        gbcImageScroll.gridx = 0;
        gbcImageScroll.gridy = 2;
        panel.add(imageScroll, gbcImageScroll);

        return panel;
    }

    public JPanel getViewerGUI() {
        return viewerGUI;
    }

    public ScrollablePicture getPicture() {
        return picture;
    }

    public DefaultListModel<IconPreview> getModel() {
        return model;
    }

    public void setFileOpenAction(Action action) {
        this.fileOpenAction = action;
    }

}