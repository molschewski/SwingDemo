package com.example.swingdemo;

import com.example.swingdemo.util.IconPreview;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.example.swingdemo.util.Utils.*;

@Component
public class Viewer {

    public static int iconSize = 128;

    protected JFrame frame;

    JMenuBar menuBar;
    JMenu menu;
    JMenuItem menuItem;

    public Viewer() {

        try {
            frame = new JFrame("Viewer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            //Add content to the window
            menuBar = createMenuBar();
            System.err.println("menuBar: " + menuBar.toString());
            frame.setJMenuBar(menuBar);
            frame.add(new ViewerGUI());

            //Display the window.
            frame.pack();
            frame.setVisible(true);
        } catch (Exception e) {
            System.err.println(e.toString());
            throw new RuntimeException(e);
        }
    }

//    public void run() {
//        frame.setVisible(true);
//    }

    public JMenuBar createMenuBar() {

        menuBar = new JMenuBar();

        menu = new JMenu("Images");
        menuBar.add(menu);

        menuItem = new JMenuItem("Open files");
        menu.add(menuItem);

        return menuBar;
    }

    public class ViewerGUI extends JPanel implements ActionListener {

        private ScrollablePicture picture;

        JButton openButton;
        JFileChooser fileChooser;
        DefaultListModel<IconPreview> model;
        JList<IconPreview> imageList;

        public ViewerGUI() throws Exception {

            super(new GridBagLayout());

            ///////////////
            // file chooser
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

            //////////
            // buttons
            GridBagConstraints gbcOpenButton = new GridBagConstraints();
            openButton = new JButton();
            openButton.setText("Open an image");
            openButton.setIcon(createImageIcon("images/Open16.gif"));
            openButton.addActionListener(this);

            ////////
            // image
            GridBagConstraints gbcPicture = new GridBagConstraints();
            picture = new ScrollablePicture(1);
            JScrollPane pictureScrollPane = new JScrollPane(picture);
            picture.setPreferredSize(new Dimension(600, 600));
//            pictureScrollPane.setPreferredSize(new Dimension(600, 600));
            pictureScrollPane.setViewportBorder(BorderFactory.createLineBorder(Color.black));
            pictureScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            pictureScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

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

//            ListCellRenderer<? super ImageIcon> cellRenderer = imageList.getCellRenderer();
//            Object cellRenderer = imageList.getCellRenderer();
            ListCellRenderer<? super IconPreview> cellRenderer = imageList.getCellRenderer();
            if (cellRenderer instanceof IconCellRenderer) {
                System.err.println("IconCellRenderer PreferredSize: "
                        + ((IconCellRenderer) cellRenderer).getPreferredSize().toString());
            }

            JScrollPane imageScroll = new JScrollPane(
                    imageList,
                    JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
            );

            imageScroll.setPreferredSize(new Dimension(600, 100));

            // debug
            int size = imageList.getModel().getSize();
            System.err.println("list.size: " + size);
            System.err.println("model.size: " + model.getSize());
            imageList.setSelectedIndex(1);

            // layout
            this.add(menuBar);

            gbcOpenButton.gridx = 0;
            gbcOpenButton.gridy = 0;
            this.add(openButton, gbcOpenButton);
//            gbcPicture.fill = GridBagConstraints.BOTH;
            gbcPicture.weightx = 0.5;
            gbcPicture.weighty = 0.5;
            gbcPicture.gridx = 0;
            gbcPicture.gridy = 1;
            this.add(picture, gbcPicture);
//            gbcImageScroll.fill = GridBagConstraints.VERTICAL;
            gbcImageScroll.weightx = 1.0;
            gbcImageScroll.weighty = 1.0;
            gbcImageScroll.gridx = 0;
            gbcImageScroll.gridy = 2;
            this.add(imageScroll, gbcImageScroll);
        }

        public void actionPerformed(ActionEvent e) {

            //Handle open button action.
            if (e.getSource() == openButton) {

                int returnVal = fileChooser.showOpenDialog(ViewerGUI.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
//                    File file = fileChooser.getSelectedFile();
//                    File dir = file.getParentFile();
                    File dir = fileChooser.getSelectedFile();
                    BufferedImage img = null;

                    System.err.println("ActionListener picture preferred size: " + picture.getPreferredSize().toString()); // debug

                    try {
                        setIconsForModel(dir);
                        if (model.isEmpty()) {
                            return;
                        }

                        Path path = model.firstElement().getPath();
                        byte[] imgBytes = Files.readAllBytes(path);
                        picture.setIcon(new ImageIcon(imgBytes));
                        picture.revalidate();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }

        public void setIconsForModel(File dir) throws IOException {
            List<File> files = readImageFiles(dir);
            model.removeAllElements();
            for (int i = 0; i < files.size(); i++) {
                File file = files.get(i);
                model.addElement(new IconPreview(file.toPath()));
            }
        }

    }
}