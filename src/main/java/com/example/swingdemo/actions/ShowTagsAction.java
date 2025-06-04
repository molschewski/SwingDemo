package com.example.swingdemo.actions;

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
        putValue(SHORT_DESCRIPTION, "Show tags");
        putValue(SMALL_ICON, createImageIcon("images/icon_tags.png"));
        putValue(LONG_DESCRIPTION, "Show the tag window");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (viewer == null) {
            System.err.println("No viewer found, giving up!");
            log.log(Level.SEVERE, "Calling ShowTagsAction without a viewer class.");
            return;
        }

        JToggleButton sourceButton =  (JToggleButton) e.getSource();
        boolean selected = sourceButton.getModel().isSelected();

//        System.out.println("Action.selected: " + selected);

        BorderLayout layout = (BorderLayout) viewer.getViewerPanel().getLayout();
        Component layoutComponent = layout.getLayoutComponent(BorderLayout.CENTER);
        viewer.getViewerPanel().remove(layoutComponent);

        // debug
//        JScrollPane tagsListPane = viewer.getTagsListScrollPane();
//        JViewport tagsListViewport = tagsListPane.getViewport();
//        System.err.println(tagsListViewport.toString());
//        for (Component component : tagsListPane.getComponents()) {
//            if (component instanceof JList<?>) {
//                System.err.println(component.toString());
//                JList list = (JList) component;
//                ListModel model = list.getModel();
//                for (int i = 0; i < model.getSize(); ++i) {
//                    System.err.println(model.getElementAt(i));
//                }
//            }
//        }

        if (selected) {
//            resetButtons(sourceButton, viewer);
            viewer.getButtonGroup().setSelected(sourceButton);

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
