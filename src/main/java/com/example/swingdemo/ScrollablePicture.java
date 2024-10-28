package com.example.swingdemo;

import java.awt.*;
        import java.awt.event.*;
        import javax.swing.*;

public class ScrollablePicture extends JLabel implements Scrollable, MouseMotionListener {

    private int maxUnitIncrement = 1;

    public ScrollablePicture(int m) {

        super();

        setText("No picture");
        setHorizontalAlignment(CENTER);
        setOpaque(true);
        setBackground(Color.white);

        maxUnitIncrement = m;

        //Let the user scroll by dragging to outside the window.
        setAutoscrolls(true); //enable synthetic drag events
        addMouseMotionListener(this); //handle mouse drags
    }

    //Methods required by the MouseMotionListener interface:
    public void mouseMoved(MouseEvent e) { }
    public void mouseDragged(MouseEvent e) {

        // debug
//        System.err.println("Enter mouseDragged");

        Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
        scrollRectToVisible(r);
    }

    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect,
                                          int orientation,
                                          int direction) {
        //Get the current position.
        int currentPosition = 0;
        if (orientation == SwingConstants.HORIZONTAL) {
            currentPosition = visibleRect.x;
        } else {
            currentPosition = visibleRect.y;
        }

        //Return the number of pixels between currentPosition
        //and the nearest tick mark in the indicated direction.
        if (direction < 0) {
            int newPosition = currentPosition -
                    (currentPosition / maxUnitIncrement)
                            * maxUnitIncrement;
            return (newPosition == 0) ? maxUnitIncrement : newPosition;
        } else {
            return ((currentPosition / maxUnitIncrement) + 1)
                    * maxUnitIncrement
                    - currentPosition;
        }
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect,
                                           int orientation,
                                           int direction) {
        if (orientation == SwingConstants.HORIZONTAL) {
            return visibleRect.width - maxUnitIncrement;
        } else {
            return visibleRect.height - maxUnitIncrement;
        }
    }

    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    public void setMaxUnitIncrement(int pixels) {
        maxUnitIncrement = pixels;
    }
}