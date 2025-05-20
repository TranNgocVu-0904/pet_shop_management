package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.function.Consumer;

public class TableButtonEditor<T> extends DefaultCellEditor {
    private final JButton button;
    private boolean clicked;
    private final JTable table;
    private final Consumer<T> onEdit;
    private final Consumer<T> onDelete;
    private final String action;
    private final RowMapper<T> mapper;

    public TableButtonEditor(JTable table, String action, RowMapper<T> mapper,
                             Consumer<T> onEdit, Consumer<T> onDelete) {
        super(new JCheckBox());
        this.table = table;
        this.action = action;
        this.mapper = mapper;
        this.onEdit = onEdit;
        this.onDelete = onDelete;
        button = new JButton(action.equals("update") ? "✏️" : "🗑️");
        button.setOpaque(true);
        button.addActionListener(e -> fireEditingStopped());
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        clicked = true;
        return button;
    }

    public Object getCellEditorValue() {
        if (clicked) {
            int row = table.getSelectedRow();
            T entity = mapper.mapRow((DefaultTableModel) table.getModel(), row);
            if (action.equals("update")) {
                onEdit.accept(entity);
            } else {
                onDelete.accept(entity);
            }
        }
        clicked = false;
        return null;
    }

    public boolean stopCellEditing() {
        clicked = false;
        return super.stopCellEditing();
    }
}
