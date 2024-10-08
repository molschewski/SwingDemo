/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.example.swingdemo;

import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URL;

/*
     * ButtonDemoSwing.java requires the following files:
     *   images/right.gif
     *   images/middle.gif
     *   images/left.gif
     */

@Component

public class ButtonDemoSwing extends JPanel {

    protected JFrame frame;

    public ButtonDemoSwing() {

        //Create and set up the window.
        frame = new JFrame("ButtonDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        ButtonDemoSwingPanel newContentPane = new ButtonDemoSwingPanel();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public class ButtonDemoSwingPanel extends JPanel implements ActionListener {

        protected JButton b1, b2, b3;

        protected ButtonDemoSwingPanel() {

            ImageIcon leftButtonIcon = createImageIcon("images/left.gif");
            ImageIcon middleButtonIcon = createImageIcon("images/middle.gif");
            ImageIcon rightButtonIcon = createImageIcon("images/right.gif");

            b1 = new JButton("Disable middle button", leftButtonIcon);
            b1.setVerticalTextPosition(AbstractButton.CENTER);
            b1.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
            b1.setMnemonic(KeyEvent.VK_D);
            b1.setActionCommand("disable");

            b2 = new JButton("Middle button", middleButtonIcon);
            b2.setVerticalTextPosition(AbstractButton.BOTTOM);
            b2.setHorizontalTextPosition(AbstractButton.CENTER);
            b2.setMnemonic(KeyEvent.VK_M);

            b3 = new JButton("Enable middle button", rightButtonIcon);
            //Use the default text position of CENTER, TRAILING (RIGHT).
            b3.setMnemonic(KeyEvent.VK_E);
            b3.setActionCommand("enable");
            b3.setEnabled(false);

            //Listen for actions on buttons 1 and 3.
            b1.addActionListener(this);
            b3.addActionListener(this);

            b1.setToolTipText("Click this button to disable the middle button.");
            b2.setToolTipText("This middle button does nothing when you click it.");
            b3.setToolTipText("Click this button to enable the middle button.");

            //Add Components to this container, using the default FlowLayout.
            add(b1);
            add(b2);
            add(b3);
        }

        public void actionPerformed(ActionEvent e) {
            if ("disable".equals(e.getActionCommand())) {
                b2.setEnabled(false);
                b1.setEnabled(false);
                b3.setEnabled(true);
            } else {
                b2.setEnabled(true);
                b1.setEnabled(true);
                b3.setEnabled(false);
            }
        }

        /** Returns an ImageIcon, or null if the path was invalid. */
        protected ImageIcon createImageIcon(String path) {

//            ClassLoader classLoader = ButtonDemoSwingPanel.class.getClassLoader();
//            System.err.println("classLoader name: " + classLoader.getName()
//                    + ", classLoader parent: " + classLoader.getParent().getName()
//                    + ", classLoader hashCode: " + classLoader.hashCode());

//            ClassLoader classLoader2 = Thread.currentThread().getContextClassLoader();
//            System.err.println("classLoader2 name: " + classLoader2.getName()
//                    + ", classLoader2 parent: " + classLoader2.getParent().getName()
//                    + ", classLoader2 hashCode: " + classLoader2.hashCode());

            /*
            * Lot of debug stuff her.
            * The lines 1 - 3 uses intern a full qualified path, that's a problem
            * Line 4 works, but is not thread safe
            * In a real application use line 5 "Thread.currentThread()..."
            */
            URL imgURL = null;
//            imgURL = ButtonDemoSwingPanel.class.getResource(path); // Doesn't work 1

            Class<ButtonDemoSwingPanel> buttonDemoSwingPanelClass = ButtonDemoSwingPanel.class;// Doesn't work
            imgURL = buttonDemoSwingPanelClass.getResource(path); // Step Into doesn't work her!? 2

            imgURL = this.getClass().getResource(path); // Doesn't work 3
//            imgURL= ClassLoader.getSystemClassLoader().getResource(path); 4

            imgURL = Thread.currentThread().getContextClassLoader().getResource(path); // 5
            if (imgURL != null) {
                return new ImageIcon(imgURL);
            } else {
                System.err.println("Couldn't find file: " + path);
                return null;
            }
        }
    }

}