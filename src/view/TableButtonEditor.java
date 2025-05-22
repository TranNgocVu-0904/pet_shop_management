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
        this.button = new JButton();

        // Label can be dynamic or passed from outside — simplified for clarity
        switch (action) {
            case "update" -> button.setText("✏️");
            case "delete" -> button.setText("🗑️");
            case "choose" -> button.setText("🛒");
            default -> button.setText("?");
        }

        button.setOpaque(true);
        button.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        clicked = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (clicked) {
            int row = table.getSelectedRow();
            T entity = mapper.mapRow((DefaultTableModel) table.getModel(), row);

            switch (action) {
                case "update" -> {
                    if (onEdit != null) onEdit.accept(entity);
                }
                case "delete" -> {
                    if (onDelete != null) onDelete.accept(entity);
                }
                case "choose" -> {
                    if (onEdit != null) onEdit.accept(entity); // Treat `onEdit` as generic handler
                }
                default -> System.err.println("Unsupported action: " + action);
            }
        }
        clicked = false;
        return null;
    }

    @Override
    public boolean stopCellEditing() {
        clicked = false;
        return super.stopCellEditing();
    }
}
