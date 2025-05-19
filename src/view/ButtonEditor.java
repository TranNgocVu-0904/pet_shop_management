package view;

import controller.PetController;
import model.Cat;
import model.Dog;
import model.Pet;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;

public class ButtonEditor extends DefaultCellEditor {
    private final JButton button;
    private final String action;
    private boolean clicked;
    private final PetPanel parent;
    private int row;

    public ButtonEditor(JCheckBox checkBox, PetPanel parent, String action) {
        super(checkBox);
        this.parent = parent;
        this.action = action;
        this.button = new JButton();
        this.button.setOpaque(true);
        this.button.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        this.row = row;
        button.setText(action.equals("update") ? "✏️" : "🗑️");
        clicked = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (clicked) {
            DefaultTableModel model = (DefaultTableModel) parent.getPetTable().getModel();

            int id = Integer.parseInt(model.getValueAt(row, 0).toString());
            String name = model.getValueAt(row, 1).toString();
            String type = model.getValueAt(row, 2).toString();
            String breed = model.getValueAt(row, 3).toString();
            int age = Integer.parseInt(model.getValueAt(row, 4).toString());
            BigDecimal price = new BigDecimal(model.getValueAt(row, 5).toString());

            Pet pet = switch (type.toUpperCase()) {
                case "DOG" -> new Dog(name, breed, age, price);
                case "CAT" -> new Cat(name, breed, age, price);
                default -> throw new IllegalArgumentException("Unsupported type: " + type);
            };
            pet.setId(id);

            if (action.equals("update")) {
                new PetDialog(parent, pet);
            } else {
                int confirm = JOptionPane.showConfirmDialog(parent,
                        "Are you sure you want to delete Pet ID " + id + "?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    PetController.deletePet(id);
                    parent.refreshTable();
                }
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
