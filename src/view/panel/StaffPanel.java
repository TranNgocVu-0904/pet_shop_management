package view.panel;

import controller.user.UserController;
import model.user.Staff;
import model.user.Manager;
import controller.user.AuthController;
import util.ui.ButtonCellRenderer;
import util.ui.ButtonCellEditor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import view.dialog.StaffFormDialog;

public class StaffPanel extends JPanel {
    private JTable staffTable;
    private DefaultTableModel model;
    private JTextField searchField;
    private final UserController controller = new UserController();

    public StaffPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 236, 236));
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // === Top Section ===
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(getBackground());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

        JButton addBtn = createRoundedButton("+");
        addBtn.setPreferredSize(new Dimension(50, 40));
        addBtn.setEnabled(AuthController.currentUser instanceof Manager); // Manager-only
        topPanel.add(addBtn, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        searchPanel.setBackground(getBackground());
        searchField = createTextField("Enter ID...", 200, 35);
        JButton searchBtn = createRoundedButton("Search");
        searchBtn.setPreferredSize(new Dimension(100, 35));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);

        topPanel.add(searchPanel, BorderLayout.EAST);

        // === Table Setup ===
//        String[] columns = {"ID", "Name", "Email", "Phone", "Username", "Salary", "Update", "Delete"};
        String[] columns = {"ID", "Name", "Email", "Phone", "Username", "Salary", "Update"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return col == 6 || col == 7;
            }
        };

        staffTable = new JTable(model);
        staffTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(staffTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        staffTable.getColumn("Update").setCellRenderer(new ButtonCellRenderer("✏️"));
//        staffTable.getColumn("Delete").setCellRenderer(new ButtonCellRenderer("🗑️"));

        staffTable.getColumn("Update").setCellEditor(new ButtonCellEditor<>(
                staffTable,
                "update",
                this::mapRowToStaff,
                staff -> {
                    if (AuthController.currentUser instanceof Manager) {
                        new StaffFormDialog(this, staff);
                    } else {
                        JOptionPane.showMessageDialog(this, "Only managers can edit staff.");
                    }
                },
                null
        ));

//        staffTable.getColumn("Delete").setCellEditor(new ButtonCellEditor<>(
//                staffTable,
//                "delete",
//                this::mapRowToStaff,
//                null,
//                staff -> {
//                    if (!(AuthController.currentUser instanceof Manager)) {
//                        JOptionPane.showMessageDialog(this, "Only managers can delete staff.");
//                        return;
//                    }
//                    int confirm = JOptionPane.showConfirmDialog(this,
//                            "Delete staff ID " + staff.getId() + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
//                    if (confirm == JOptionPane.YES_OPTION) {
//                        controller.deleteStaff(staff.getId());
//                        refreshTable();
//                    }
//                }
//        ));

        addBtn.addActionListener(e -> new StaffFormDialog(this, null));

        searchBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(searchField.getText().trim());
                Staff match = controller.getAllStaff().stream()
                        .filter(s -> s.getId() == id)
                        .findFirst()
                        .orElse(null);
                model.setRowCount(0);
                if (match != null) addStaffToTable(match);
                else JOptionPane.showMessageDialog(this, "No staff found with ID " + id);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID.");
            }
        });

        loadStaff();

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadStaff() {
        model.setRowCount(0);
        controller.getAllStaff().forEach(this::addStaffToTable);
    }

//    private void addStaffToTable(Staff s) {
//        model.addRow(new Object[]{
//                s.getId(),
//                s.getName(),
//                s.getEmail(),
//                s.getPhone(),
//                s.getUsername(),
//                s.getSalary(),
//                "✏️",
//                "🗑️"
//        });
//    }
    
    private void addStaffToTable(Staff s) {
        model.addRow(new Object[]{
                s.getId(),
                s.getName(),
                s.getEmail(),
                s.getPhone(),
                s.getUsername(),
                s.getSalary(),
                "✏️"
        });
    }

    private Staff mapRowToStaff(DefaultTableModel model, int row) {
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        return controller.getAllStaff().stream()
                .filter(s -> s.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Staff not found."));
    }

    private JButton createRoundedButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(0x007BFF));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(new Color(0x0056B3), 1, true));
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0x0056B3));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0x007BFF));
            }
        });
        return button;
    }

    private JTextField createTextField(String placeholder, int width, int height) {
        JTextField field = new JTextField(placeholder);
        field.setPreferredSize(new Dimension(width, height));
        field.setForeground(Color.GRAY);
        field.setBackground(Color.WHITE);
        field.setCaretColor(Color.BLACK);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });

        return field;
    }

    public void refreshTable() {
        loadStaff();
    }

    public JTable getStaffTable() {
        return staffTable;
    }
}
