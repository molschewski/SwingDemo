package com.example.swingdemo.util;

import javax.swing.*;
import java.util.ArrayList;

/**
 * This class is a replacement for <code>ButtonGroup</code>.
 * In a <code>ButtonGroup</code> there is always one button
 * selected, here I want to select none or exact one button.
 *
 * This class only accepts <code>JToggleButton</code> as type.
 */

public class ButtonGroup1 {

    protected ArrayList<JToggleButton> buttons = new ArrayList<>();

    ButtonModel selection = null;

    public ButtonGroup1() {}

    public void add(JToggleButton button) {
        if(button == null) {
            return;
        }
        buttons.add(button);

        if (button.isSelected()) {
            if (selection == null) {
                selection = button.getModel();
            } else {
                button.setSelected(false);
            }
        }
    }

    public void setSelected(JToggleButton sourceButton) {
       for (JToggleButton button : buttons) {
           if (button.equals(sourceButton)) {
               selection = sourceButton.getModel();
           } else {
               button.setSelected(false);
               System.err.println("ButtonGroup1.button: " + button.toString());
           }
       }
    }

    public JToggleButton getSelected() {
        for (JToggleButton button : buttons) {
            if (button.isSelected()) {
                return button;
            }
        }
        return null;
    }

}
