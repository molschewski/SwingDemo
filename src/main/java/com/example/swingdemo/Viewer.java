package com.example.swingdemo;

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
import java.io.FilenameFilter;
import java.io.IOException;

import static com.example.swingdemo.Utils.createImageIcon;

@Component
public class Viewer {

    protected JFrame frame;

    public Viewer() {

        try {
            frame = new JFrame("Viewer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            //Add content to the window.
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

    public class ViewerGUI extends JPanel implements ActionListener {

        JButton openButton;
        JFileChooser fileChooser;
        FilenameFilter fileNameFilter;
        DefaultListModel<BufferedImage> model;

        private ScrollablePicture picture;

        public ViewerGUI() throws Exception {

            super(new BorderLayout());

            fileNameFilter = new FilenameFilter() {
                @Override
                public boolean accept(File file, String name) {
                    return true;
                }
            };

            // image
            picture = new ScrollablePicture(1);
            JScrollPane pictureScrollPane = new JScrollPane(picture);
            pictureScrollPane.setPreferredSize(new Dimension(500, 300));
            pictureScrollPane.setViewportBorder(BorderFactory.createLineBorder(Color.black));

            // image scroll preview
            model = new DefaultListModel<>();
            final JList<BufferedImage> imageList = new JList<>(model);
            imageList.setCellRenderer(new IconCellRenderer());
            ListSelectionListener listener = new ListSelectionListener() {

                @Override
                public void valueChanged(ListSelectionEvent lse) {
                    Object o = imageList.getSelectedValue();
                    if (o instanceof BufferedImage) {
                        picture.setIcon(new ImageIcon((BufferedImage)o));
                    }
                }
            };
            imageList.addListSelectionListener(listener);

            // file chooser
            fileChooser = new JFileChooser();
            String[] imageTypes = ImageIO.getReaderFileSuffixes();
            FileNameExtensionFilter fnf = new FileNameExtensionFilter("Images", imageTypes);
            fileChooser.setFileFilter(fnf);
//            File userHome = new File(System.getProperty("user.home"));
//            fileChooser.setSelectedFile(userHome);

            //Add the preview pane for the file chooser and set the selection mode
            fileChooser.setAccessory(new ImagePreview(fileChooser));
//            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//            fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

            // buttons
            openButton = new JButton();
            openButton.setText("Open an image");
            openButton.setIcon(createImageIcon("images/Open16.gif"));
            openButton.addActionListener(this);

            // layout
            //For layout purposes, put the buttons in a separate panel
            JPanel buttonPanel = new JPanel(); //use FlowLayout
            buttonPanel.add(openButton);

            imageList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
            imageList.setVisibleRowCount(1);

            Object cellRenderer = imageList.getCellRenderer();
//            ListCellRenderer<? super IconCellRenderer> cellRenderer = imageList.getCellRenderer();
            if (cellRenderer instanceof IconCellRenderer) {
                System.err.println("IconCellRenderer PreferredSize: "
                        + ((IconCellRenderer) cellRenderer).getPreferredSize().toString());
            }


            JPanel bottomPane = new JPanel();
            JScrollPane imageScroll = new JScrollPane(
                    imageList,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
            );

            imageScroll.setPreferredSize(new Dimension(500, 100));
            bottomPane.add(imageScroll);


            //Add the buttons and the log to this panel.
            add(buttonPanel, BorderLayout.PAGE_START);
            add(pictureScrollPane, BorderLayout.CENTER);
            add(bottomPane, BorderLayout.SOUTH);
//            add(logScrollPane, BorderLayout.SOUTH);

        }

        public void actionPerformed(ActionEvent e) {

            //Handle open button action.
            if (e.getSource() == openButton) {

                int returnVal = fileChooser.showOpenDialog(ViewerGUI.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    File dir = file.getParentFile();
                    BufferedImage img = null;
                    try {
                        loadImages(dir);
                        img = ImageIO.read(file);
                        ImageIcon icon = new ImageIcon(img);
                        picture.setIcon(icon);
                        picture.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
                        picture.revalidate();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }

        public void loadImages(File directory) throws IOException {
            File[] imageFiles = directory.listFiles(fileNameFilter);
            System.err.println("imageFiles.length: " + imageFiles.length); // debug
            model.removeAllElements();
            for (int i = 0; i < imageFiles.length; i++) {
                model.addElement(ImageIO.read(imageFiles[i]));
            }
        }
    }

}