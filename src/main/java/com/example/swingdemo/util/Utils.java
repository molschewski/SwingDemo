package com.example.swingdemo.util;

import com.example.swingdemo.Viewer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/* Utils.java is used by FileChooserDemo2.java. */
public class Utils {

    public final static String jpeg = "jpeg";
    public final static String jpg = "jpg";
    public final static String gif = "gif";
    public final static String tiff = "tiff";
    public final static String tif = "tif";
    public final static String png = "png";

    public final static HashSet<String> imageExtensions = new HashSet<>(
            Arrays.asList("gif", "jpg", "jpeg", "png", "tif", "tiff"));

    // Get the extension of a file.
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

    // Returns an ImageIcon, or null if the path was invalid.
    public static ImageIcon createImageIcon(String path) {

        URL imgURL = Thread.currentThread().getContextClassLoader().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    public static List<File> readImageFiles(File dir) throws IOException {
        String[] imageTypes = ImageIO.getReaderFileSuffixes();
        FileNameExtensionFilter fnf = new FileNameExtensionFilter("Images", imageTypes);
        try (Stream<Path> stream = Files.list(dir.toPath())) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .filter(file -> fnf.accept(file.toFile()))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        }
    }

    static public void setIconsForModel(DefaultListModel<IconPreview> model, File dir) throws IOException {
        List<File> files = readImageFiles(dir);
        model.removeAllElements();
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            model.addElement(new IconPreview(file.toPath()));
        }
    }

    /**
     * Resizes an image using a Graphics2D object backed by a BufferedImage.
     * @param srcImg - source image to scale
     * @param w - desired width
     * @param h - desired height
     * @return - the new resized image
     */
    public static Image scaleImage(Image srcImg, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = resizedImg.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(srcImg, 0, 0, w, h, null);
        graphics.dispose();
        return resizedImg;
    }

    public static void resetButtons(JToggleButton sourceButton, Viewer viewer) {
        BorderLayout borderLayout = (BorderLayout) viewer.getViewerPanel().getLayout();
        JPanel lineEndPanel = (JPanel) borderLayout.getLayoutComponent(BorderLayout.LINE_END);
        BoxLayout boxLayout = (BoxLayout) lineEndPanel.getLayout();
        for (Component component : boxLayout.getTarget().getComponents()) {
            if (component instanceof JToggleButton && component != sourceButton) {
                JToggleButton button = (JToggleButton) component;
                button.setSelected(false);
            }
        }
    }

    public static void readCivitAIInfos(Path path)  {

        byte IDENTIFIER = (byte) 0xff;
        byte[] USERCOMMENT = new byte[]{(byte) 0x92, (byte) 0x86};

        try (
            InputStream inputStream = Files.newInputStream(path);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)) {

            byte[] b = new byte[2];
            byte[] magicNumberJpg = new byte[]{(byte)0xff, (byte)0xd8};

            bufferedInputStream.mark(1024); // debug

            bufferedInputStream.read(b, 0, 2);
            System.err.println("read bytes: " + HexFormat.of().formatHex(b));

            if (!Arrays.equals(magicNumberJpg, b)) {
                System.err.println("Not a jpg");
//                log.log(Level.INFO, "Not a jpg");
                return;
            }

            bufferedInputStream.reset();
            char test1 = (char) bufferedInputStream.read();
            char test2 = (char) bufferedInputStream.read();
            System.err.println("testchar1: " + HexFormat.of().toHexDigits(test1) + " testchar2: "
                    + HexFormat.of().toHexDigits(test2));

            int offset = 2;
            int length = 1;
            int index = 2;
            byte preRead = 0;
            byte actRead = 0;
            byte[] bytesRead = new byte[9];

            long fileSize = Files.size(path);
            System.err.println("size (bytes): " + fileSize);
            byte APP1 = (byte) 0xe1;


            do {
//                        preRead = actRead;
                actRead = (byte) bufferedInputStream.read();
                if (IDENTIFIER == actRead) {
                    bufferedInputStream.mark(1024);
                    bufferedInputStream.read(bytesRead, 0, 9);
                    byte firstByte = bytesRead[0];
                    if (APP1 == firstByte) {
                        System.err.println("bytesRead first byte: " + HexFormat.of().toHexDigits(firstByte));
                        System.err.println("bytesRead second and third byte: "
                                + HexFormat.of().formatHex(bytesRead, 1, 3));

                        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
                        buffer.put(new byte[]{(byte) 0x0e, (byte) 0x7e});
                        buffer.rewind();
                        int value = buffer.getShort();
                        System.err.println("bytesRead length: " + value);
                        System.err.println("bytesRead: ff" + HexFormat.of().formatHex(bytesRead));
                    }
                    bufferedInputStream.reset();
//                            System.err.println(HexFormat.of().toHexDigits(preRead) + HexFormat.of().toHexDigits(actRead));
                }
            } while (++index < fileSize);

//                        System.err.println("preRead: " + HexFormat.of().toHexDigits(preRead)
//                                + " actRead: " + HexFormat.of().toHexDigits(actRead));
//                    } while (++index < 1000);

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}