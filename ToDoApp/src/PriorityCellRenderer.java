import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class PriorityCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (value instanceof Integer) {
            int priority = (Integer) value;
            switch (priority) {
                case 1: c.setBackground(Color.RED); break;       // High
                case 2: c.setBackground(Color.YELLOW); break;    // Medium
                case 3: c.setBackground(Color.GREEN); break;     // Low
                default: c.setBackground(Color.WHITE);
            }
        } else {
            c.setBackground(Color.WHITE);
        }

        if (isSelected) {
            c.setBackground(c.getBackground().darker());
        }

        return c;
    }
}
