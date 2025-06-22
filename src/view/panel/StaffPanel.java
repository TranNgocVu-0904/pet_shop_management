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
        String[] columns = {"ID", "Name", "Email", "Phone", "Username", "Salary", "Update", "Delete"};
        model = new DefaultTableModel(columns, 0) {
            @Override
           public boolean isCellEditable(int row, int col) {
                return col == 6 || col == 7;
            }
        };

        staffTable = new JTable(model);
        staffTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(staffTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Set Update button renderer and editor
        staffTable.getColumn("Update").setCellRenderer(new ButtonCellRenderer("✏️"));
        staffTable.getColumn("Update").setCellEditor(new ButtonCellEditor<>(
            staffTable,
            "update",
            (tableModel, row) -> {
                try {
                    int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                    Staff staff = controller.getAllStaff().stream()
                            .filter(s -> s.getId() == id)
                            .findFirst()
                            .orElse(null);
                    if (staff == null) {
                        JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên có ID " + id);
                    }
                    return staff;
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "ID không hợp lệ.");
                    return null;
                }
            },
            staff -> new StaffFormDialog(this, staff),
            null
        ));

        // Set Delete button renderer and editor
        staffTable.getColumn("Delete").setCellRenderer(new ButtonCellRenderer("🗑️"));
        staffTable.getColumn("Delete").setCellEditor(new ButtonCellEditor<>(
            staffTable,
            "delete",
            (tableModel, row) -> {
                try {
                    int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                    Staff staff = controller.getAllStaff().stream()
                            .filter(s -> s.getId() == id)
                            .findFirst()
                            .orElse(null);
                    if (staff == null) {
                        JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên có ID " + id);
                    }
                    return staff;
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "ID không hợp lệ.");
                    return null;
                }
            },
            null,
            staff -> {
                if (staff == null) return;

                if (!(AuthController.currentUser instanceof Manager)) {
                    JOptionPane.showMessageDialog(this, "Chỉ quản lý mới có quyền xóa nhân viên.");
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Bạn có chắc chắn muốn xóa nhân viên ID " + staff.getId() + "?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        boolean success = controller.deleteStaff(staff.getId());
                        if (success) {
                            JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công.");
                            refreshTable();
                        } else {
                            JOptionPane.showMessageDialog(this, "Không thể xóa nhân viên (có thể do ràng buộc khóa ngoại).");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Lỗi khi xóa nhân viên.");
                    }
                }
            }
        ));

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

        // Bỏ chọn tất cả hàng trong bảng
        staffTable.clearSelection();
}
    private void addStaffToTable(Staff s) {
        model.addRow(new Object[]{
                s.getId(),
                s.getName(),
                s.getEmail(),
                s.getPhone(),
                s.getUsername(),
                s.getSalary(),
                "✏️",
                "🗑️"
        });
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
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0x0056B3));
            }

            @Override
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
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
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
