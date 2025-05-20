package view;

import javax.swing.table.DefaultTableModel;

public interface RowMapper<T> {
    T mapRow(DefaultTableModel model, int row);
}
