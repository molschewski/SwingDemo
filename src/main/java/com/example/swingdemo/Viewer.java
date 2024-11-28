package com.example.swingdemo;

import com.example.swingdemo.util.FileOpenAction;
import com.example.swingdemo.util.IconPreview;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.example.swingdemo.util.Utils.*;

@Component
public class Viewer {

    public static int iconSize = 128;

    protected JFrame frame;

    private JPanel viewerGUI;
    private ScrollablePicture picture;
    private JScrollPane pictureScrollPane;
    private JButton openButton;
    private DefaultListModel<IconPreview> model;
    private JList<IconPreview> imageList;
    private Action fileOpenAction;

    JMenuBar menuBar;
    JMenu menu;
    JMenuItem menuItem;

    Action testAction;

    public Viewer() {

    }

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

        menuBar = new JMenuBar();

        menu = new JMenu("Images");
        menuBar.add(menu);

        menuItem = new JMenuItem(fileOpenAction);
        menu.add(menuItem);

        return menuBar;
    }

    public JPanel createViewerGUI() throws Exception {

        JPanel panel = new JPanel(new GridBagLayout());

        //////////
        // buttons
        GridBagConstraints gbcOpenButton = new GridBagConstraints();
        openButton = new JButton(fileOpenAction);
//        openButton.setText("Open an image");
        openButton.setIcon(createImageIcon("images/Open16.gif"));
//        openButton.setAction(fileOpenAction);

        ////////
        // image
        GridBagConstraints gbcPictureScrollPane = new GridBagConstraints();
        picture = new ScrollablePicture(1);
        pictureScrollPane = new JScrollPane(picture);
//        picture.setPreferredSize(new Dimension(600, 600));
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
                    Path path = selectedValue.getPath();
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

        ListCellRenderer<? super IconPreview> cellRenderer = imageList.getCellRenderer();
//        if (cellRenderer instanceof IconCellRenderer) {
//            System.err.println("IconCellRenderer PreferredSize: "
//                    + ((IconCellRenderer) cellRenderer).getPreferredSize().toString());
//        }

        JScrollPane imageScroll = new JScrollPane(
                imageList,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        imageScroll.setPreferredSize(new Dimension(600, 100));

//        // debug
//        int size = imageList.getModel().getSize();
//        System.err.println("list.size: " + size);
//        System.err.println("model.size: " + model.getSize());
//        imageList.setSelectedIndex(1);

        gbcOpenButton.gridx = 0;
        gbcOpenButton.gridy = 0;
        gbcOpenButton.anchor = GridBagConstraints.LINE_START;
        panel.add(openButton, gbcOpenButton);
//            gbcPicture.fill = GridBagConstraints.BOTH;
        gbcPictureScrollPane.weightx = 0.5;
        gbcPictureScrollPane.weighty = 0.5;
        gbcPictureScrollPane.gridx = 0;
        gbcPictureScrollPane.gridy = 1;
        panel.add(pictureScrollPane, gbcPictureScrollPane);
//            gbcImageScroll.fill = GridBagConstraints.VERTICAL;
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