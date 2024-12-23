package com.example.swingdemo.util;

import com.example.swingdemo.Viewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.example.swingdemo.util.Utils.createImageIcon;

public class ShowTagsAction extends AbstractAction {

    Logger log = Logger.getLogger(ShowTagsAction.class.getName());

    private Viewer viewer;

    public ShowTagsAction(Viewer viewer) {
        super();
        this.viewer = viewer;
        putValue(NAME, "Show tags");
        putValue(SMALL_ICON, createImageIcon("images/Open16.gif"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (viewer == null) {
            System.err.println("No viewer found, giving up!");
            log.log(Level.SEVERE, "Calling ShowTagsAction without a viewer class.");
            return;
        }

        AbstractButton abstractButton =  (AbstractButton)e.getSource();
        boolean selected = abstractButton.getModel().isSelected();

//        System.out.println("Action.selected: " + selected);

        BorderLayout layout = (BorderLayout) viewer.getViewerPanel().getLayout();
        Component layoutComponent = layout.getLayoutComponent(BorderLayout.CENTER);
        viewer.getViewerPanel().remove(layoutComponent);

        if (selected) {
            JSplitPane splitPane = new JSplitPane();
            splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
            splitPane.setTopComponent(viewer.getTagsListScrollPane());
            splitPane.setBottomComponent(viewer.getImagePanel());
            viewer.getViewerPanel().add(splitPane, BorderLayout.CENTER);
            viewer.setSplitPane(splitPane);
        } else {
            viewer.getViewerPanel().add(viewer.getImagePanel(), BorderLayout.CENTER);
        }

        viewer.getViewerPanel().revalidate();
    }
}
