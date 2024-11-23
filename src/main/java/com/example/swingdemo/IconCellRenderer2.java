package com.example.swingdemo;

import com.example.swingdemo.util.IconPreview;

import javax.swing.*;
import java.awt.*;

public class IconCellRenderer2 extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;

    private int size;
    private ImageIcon icon;

    IconCellRenderer2() {
        this(100);
    }

    IconCellRenderer2(int size) {
        this.size = size;
    }

    @Override
    public Component getListCellRendererComponent (
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (component instanceof JLabel && value instanceof ImageIcon) {
            JLabel label = (JLabel)component;
            label.setText("");
            label.setIcon((Icon) value);
        } else if (component instanceof JLabel && value instanceof IconPreview) {
            System.err.println("IconCellRenderer found instance of IconPreview");

            IconPreview iconPreview = (IconPreview)value;
            icon = iconPreview.getIcon();

            JLabel label = (JLabel)component;
            label.setText("");
            label.setIcon(icon);

//            BufferedImage read = ImageIO.read();
//                Image image = scaleImage(read, 100, 100);
        }
        return component;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(size, size);
    }
}
