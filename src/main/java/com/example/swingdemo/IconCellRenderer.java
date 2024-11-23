package com.example.swingdemo;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class IconCellRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;

    private int size;
    private BufferedImage icon;

    IconCellRenderer() {
        this(100);
    }

    IconCellRenderer(int size) {
        this.size = size;
        icon = new BufferedImage(size,size,BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    public java.awt.Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        java.awt.Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (component instanceof JLabel && value instanceof BufferedImage) {
            JLabel label = (JLabel)component;
            label.setText("");
            BufferedImage image = (BufferedImage)value;
            label.setIcon(new ImageIcon(icon));

            Graphics2D graphics = icon.createGraphics();
            graphics.setColor(new Color(0,0,0,0));
            graphics.clearRect(0, 0, size, size);
            graphics.drawImage(image,0,0,size,size,this);
            graphics.dispose();
        }
        return component;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(size, size);
    }
}
