package util;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class TableHelper {
    public static void configureTable(JTable table, String[] columns) {
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setModel(model);
        table.setAutoCreateRowSorter(true);
    }
}