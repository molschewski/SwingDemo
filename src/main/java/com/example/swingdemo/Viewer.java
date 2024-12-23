package com.example.swingdemo;

import com.example.swingdemo.util.FileOpenAction;
import com.example.swingdemo.util.IconPreview;
import com.example.swingdemo.util.ShowTagsAction;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Vector;

@Component
public class Viewer {

    public static int iconSize = 128;

    private JPanel viewerPanel;
    private JSplitPane splitPane;
    private JScrollPane tagsListScrollPane;
    private JPanel imagePanel;
    private ScrollablePicture picture;
    private DefaultListModel<IconPreview> imageListModel;
    private JList<IconPreview> imageList;
    private JList tagsList;
    private Action fileOpenAction;


    private Action showTagsAction;
    private JMenuBar menuBar;

    private Vector testData = new Vector<>(Arrays.asList("foo", "bar", "boo"));

    public Viewer() {

    }

    @Bean
    public static void createViewer() {
        JFrame frame = new JFrame("Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Viewer viewer = new Viewer();
        viewer.setFileOpenAction(new FileOpenAction(viewer));
        viewer.setShowTagsAction(new ShowTagsAction(viewer));

        //Add content to the window
        try {
            viewer.menuBar = viewer.createMenuBar();
            frame.setJMenuBar(viewer.menuBar);
            viewer.viewerPanel = viewer.createViewerGUI();
            frame.add(viewer.viewerPanel);
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

        viewerPanel = new JPanel(new BorderLayout());
        JPanel lineEndPanel = new JPanel();
        lineEndPanel.setLayout(new BoxLayout(lineEndPanel, BoxLayout.PAGE_AXIS));
        imagePanel = new JPanel(new GridBagLayout());
        tagsListScrollPane = new JScrollPane();
        JScrollPane pictureScrollPane = new JScrollPane();
        splitPane = new JSplitPane();

        //debug
//        centerPanel.addComponentListener(new ResizeListener("centerPanel"));
        imagePanel.addComponentListener(new ResizeListener("imagePanel"));

        //////////
        // buttons
        JButton openButton = new JButton(fileOpenAction);

        JToggleButton showTagsButton = new JToggleButton(showTagsAction);

//        PropertyChangeListener[] propertyChangeListeners = openButton.getPropertyChangeListeners();
//        for (PropertyChangeListener pcl : propertyChangeListeners) {
//            System.err.println("PropertyChangeListener: " + pcl.toString());
//        }
//        Arrays.stream(openButton.getChangeListeners()).toList();

        // debug
//        openButton.addPropertyChangeListener(new PropertyChangeListener() {
//            public void propertyChange(PropertyChangeEvent evt) {
//                if (evt.getPropertyName().equals("enabled")) {
//                    System.err.println("openButton Event: " + evt.toString());
//                }
////                    boolean isEnabled = (Boolean)evt.getNewValue();
////                    for (AbstractButton button : buttons) {
////                        button.setEnabled(isEnabled);
////                    }
////                }
//            }
//        });

        ////////////
        // tagsPanel
        tagsList = new JList();
        tagsList.setListData(testData);
        tagsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tagsList.setLayoutOrientation(JList.HORIZONTAL_WRAP);

        tagsList.setVisibleRowCount(-1);
//        tagsList.addMouseListener(new MouseAdapter() {
//            public void mouseClicked(MouseEvent e) {
//                if (e.getClickCount() == 2) {
//                    buttonName.doClick(); //emulate button click
//                }
//            }
//        });
        tagsListScrollPane.add(tagsList);
        tagsListScrollPane.setPreferredSize(new Dimension(600, 80));

        ////////
        // image
        picture = new ScrollablePicture(1);
        pictureScrollPane.setViewportView(picture);
        pictureScrollPane.setPreferredSize(new Dimension(600, 600));
        pictureScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        pictureScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        pictureScrollPane.setViewportBorder(BorderFactory.createLineBorder(Color.black));

        ///////////////////////
        // image scroll preview
        imageListModel = new DefaultListModel<>();
        imageList = new JList<>(imageListModel);
        imageList.setCellRenderer(new IconCellRenderer2());
        ListSelectionListener listener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {

                System.err.println("ListSelectionEvent: " + lse);

                IconPreview selectedValue = imageList.getSelectedValue();
                try {
                    if (selectedValue == null) {
                        System.err.println("ListSelectionEvent: selectedValue is null");
                        return;
                    }
                    Path path = selectedValue.getPath();
                    if (path == null) {
                        System.err.println("ListSelectionEvent: path is null");
                        // do nothing till I have a nice placeholder
                        return;
                    }
                    System.err.println("ListSelectionEvent: set image");
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
        imageScroll.setMinimumSize(new Dimension(100, 100));

               ///////////////
        // lineEndPanel
        lineEndPanel.add(openButton);
        lineEndPanel.add(showTagsButton);

        /////////////
        // imagePanel
        GridBagConstraints gbcPictureScrollPane = new GridBagConstraints();
        gbcPictureScrollPane.weightx = 0.5;
        gbcPictureScrollPane.weighty = 0.5;
        gbcPictureScrollPane.gridx = 0;
        gbcPictureScrollPane.gridy = 0;
        gbcPictureScrollPane.fill = GridBagConstraints.BOTH;
        imagePanel.add(pictureScrollPane, gbcPictureScrollPane);

        GridBagConstraints gbcImageScroll = new GridBagConstraints();
        gbcImageScroll.weightx = 0.0;
        gbcImageScroll.weighty = 0.0;
        gbcImageScroll.gridx = 0;
        gbcImageScroll.gridy = 1;
        gbcImageScroll.fill = GridBagConstraints.HORIZONTAL;
        imagePanel.add(imageScroll, gbcImageScroll);

        viewerPanel.add(lineEndPanel, BorderLayout.LINE_END);
        viewerPanel.add(imagePanel, BorderLayout.CENTER);

        return viewerPanel;
    }

    class ResizeListener extends ComponentAdapter {

        String name;

        public ResizeListener(String name) {
            super();
            this.name = name;
        }

        public void componentResized(ComponentEvent e) {
            System.err.println(
                    name + ".height: " + e.getComponent().getHeight()
                    + ", " + name + ".width: " + e.getComponent().getWidth());
        }
    }

    public JPanel getViewerPanel() {
        return viewerPanel;
    }

    public JPanel getImagePanel() {
        return imagePanel;
    }

    public JScrollPane getTagsListScrollPane() {
        return tagsListScrollPane;
    }

    public JSplitPane getSplitPane() {
        return splitPane;
    }

    public void setSplitPane(JSplitPane splitPane) {
        this.splitPane = splitPane;
    }

    public ScrollablePicture getPicture() {
        return picture;
    }

    public DefaultListModel<IconPreview> getImageListModel() {
        return imageListModel;
    }

    public void setFileOpenAction(Action action) {
        this.fileOpenAction = action;
    }

    public void setShowTagsAction(Action showTagsAction) {
        this.showTagsAction = showTagsAction;
    }

}