
package weshampson.gtascreens.gui;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import weshampson.gtascreens.screenshot.Screenshot;

/**
 *
 * @author  Wes Hampson
 * @version 1.0.0 (Sep 6, 2014)
 * @since   1.0.0 (Aug 28, 2014)
 */
public class ScreenshotCellRenderer implements ListCellRenderer<Screenshot> {
    private final DefaultListCellRenderer defaultListCellRenderer = new DefaultListCellRenderer();
    @Override
    public Component getListCellRendererComponent(JList<? extends Screenshot> list, Screenshot value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel)defaultListCellRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        label.setText(value.getDisplayName() + " (" + Math.round((float)value.getFileSize() / 1024.0) + " kB)");
        if (isSelected) {
            label.setBackground(list.getSelectionBackground());
        }
        return(label);
    }

}
