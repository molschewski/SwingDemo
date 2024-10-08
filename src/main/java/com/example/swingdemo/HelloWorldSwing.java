package com.example.swingdemo;

import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

//@Component
public class HelloWorldSwing extends JFrame {

    public HelloWorldSwing() {
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("HelloWorldSwing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create the menu bar.  Make it have a green background.
        JMenuBar greenMenuBar = new JMenuBar();
        greenMenuBar.setOpaque(true);
        greenMenuBar.setBackground(new Color(154, 165, 127));
        greenMenuBar.setPreferredSize(new Dimension(200, 20));

        //Create a yellow label to put in the content pane.
        JLabel yellowLabel = new JLabel();
        yellowLabel.setOpaque(true);
        yellowLabel.setText("Hello World");
        yellowLabel.setForeground(new Color(0, 0, 0));
        yellowLabel.setBackground(new Color(248, 213, 131));
        yellowLabel.setPreferredSize(new Dimension(200, 180));

        //Set the menu bar and add the label to the content pane.
        setJMenuBar(greenMenuBar);
        getContentPane().add(yellowLabel, BorderLayout.CENTER);

        setTitle("Hello World");
//        setSize(300, 200);

        //Display the window.
        pack();
        setVisible(true);
    }
}


