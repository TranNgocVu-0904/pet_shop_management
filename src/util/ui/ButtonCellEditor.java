package util.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.function.Consumer;

public class ButtonCellEditor<T> extends DefaultCellEditor {
    private final JButton button;
    private boolean clicked;
    private final JTable table;
    private final Consumer<T> onEdit;
    private final Consumer<T> onDelete;
    private final String action;
    private final RowMapper<T> mapper;

    public ButtonCellEditor(JTable table, String action, RowMapper<T> mapper,
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
        button.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                getCellEditorValue();      // Triggers action
                fireEditingStopped();      // Ends editing cleanly
            });
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        clicked = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        int row = table.getSelectedRow();

        // Defensive check
        if (row < 0 || row >= table.getRowCount()) return null;

        // Defer all actions to allow table state to settle
        SwingUtilities.invokeLater(() -> {
            T entity = mapper.mapRow((DefaultTableModel) table.getModel(), row);

            switch (action) {
                case "update" -> {
                    if (onEdit != null) onEdit.accept(entity);
                }
                case "delete" -> {
                    if (onDelete != null) onDelete.accept(entity);
                }
                case "choose" -> {
                    if (onEdit != null) onEdit.accept(entity); // generic choose handler
                }
                default -> System.err.println("Unsupported action: " + action);
            }
        });

        return null;
    }

    @Override
    public boolean stopCellEditing() {
        clicked = false;
        return super.stopCellEditing();
    }
}
