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
        java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (c instanceof JLabel && value instanceof BufferedImage) {
            JLabel l = (JLabel)c;
            l.setText("");
            BufferedImage i = (BufferedImage)value;
            l.setIcon(new ImageIcon(icon));

            Graphics2D g = icon.createGraphics();
            g.setColor(new Color(0,0,0,0));
            g.clearRect(0, 0, size, size);
            g.drawImage(i,0,0,size,size,this);

            g.dispose();
        }
        return c;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(size, size);
    }
}
