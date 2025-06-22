package view.panel;

import util.ui.ButtonCellRenderer;
import util.ui.ButtonCellEditor;
import controller.customer.CustomerController;
import model.user.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Optional;
import view.dialog.CustomerFormDialog;

public class CustomerPanel extends JPanel {
    private JTable customerTable;
    private DefaultTableModel model;
    private JTextField searchField;
    private JComboBox<String> sortBox;

    public CustomerPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 236, 236));
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(getBackground());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

        // === Left (Add)
        JButton addBtn = createRoundedButton("+");
        addBtn.setPreferredSize(new Dimension(50, 40));
        topPanel.add(addBtn, BorderLayout.WEST);

        // === Right (Search/Filter)
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        filterPanel.setBackground(getBackground());

        searchField = createTextField("Enter ID...", 200, 35);
        JButton searchBtn = createRoundedButton("Search");
        searchBtn.setPreferredSize(new Dimension(100, 35));

        sortBox = new JComboBox<>(new String[]{"None", "ASC", "DESC"});
        styleComboBox(sortBox);

        filterPanel.add(searchField);
        filterPanel.add(searchBtn);
        filterPanel.add(sortBox);

        topPanel.add(filterPanel, BorderLayout.EAST);

        // === Table Setup
        String[] columns = {"ID", "Name", "Email", "Phone", "Loyalty", "✏️", "🗑️"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col >= 5;
            }
        };

        customerTable = new JTable(model);
        customerTable.setRowHeight(30);

        customerTable.getColumn("✏️").setCellRenderer(new ButtonCellRenderer("✏️"));
        customerTable.getColumn("🗑️").setCellRenderer(new ButtonCellRenderer("🗑️"));

customerTable.getColumn("✏️").setCellEditor(new ButtonCellEditor<>(
    customerTable,
    "update",
    (model, row) -> {
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        return CustomerController.getCustomerById(id); // trả về Optional<Customer>
    },
    customerOpt -> {
        if (customerOpt.isPresent()) 
        {
            CustomerFormDialog customerFormDialog = new CustomerFormDialog(this, customerOpt.get());
        } 
        else 
        {
            JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng.");
        }
    },
    null
));


customerTable.getColumn("🗑️").setCellEditor(new ButtonCellEditor<>(
    customerTable,
    "delete",
    (model, row) -> {
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        return CustomerController.getCustomerById(id);  // trả về Optional<Customer>
    },
    null,
    optionalCustomer -> {
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa khách hàng ID " + customer.getId() + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = CustomerController.deleteCustomer(customer.getId());
                if (success) {
                    JOptionPane.showMessageDialog(this, "Xóa khách hàng thành công.");
                    refreshTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa khách hàng (có thể do ràng buộc khóa ngoại).");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng để xóa.");
        }
    }
));


        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // === Action Listeners
        addBtn.addActionListener(e -> new CustomerFormDialog(this, null));

        searchBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(searchField.getText().trim());
                Optional<Customer> c = CustomerController.getCustomerById(id);
                model.setRowCount(0);
                if (c.isPresent()) addCustomerToTable(c.get());
else JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng.");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID không hợp lệ.");
            }
        });

        sortBox.addActionListener(e -> applyFilter());

        loadAllCustomers();

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadAllCustomers() {
        model.setRowCount(0);
        CustomerController.getAllCustomers().forEach(this::addCustomerToTable);
    }

    private void applyFilter() {
        String order = sortBox.getSelectedItem().toString();
        if (order.equals("None")) {
            loadAllCustomers();
            return;
        }
        model.setRowCount(0);
        CustomerController.getCustomersByLoyalty(order).forEach(this::addCustomerToTable);
    }

    private void addCustomerToTable(Customer c) {
        model.addRow(new Object[]{
                c.getId(), c.getName(), c.getEmail(), c.getPhone(), c.getLoyaltyPoints(), "✏️", "🗑️"
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
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });

        return field;
    }

    private void styleComboBox(JComboBox<String> box) {
        box.setPreferredSize(new Dimension(120, 35));
        box.setBackground(Color.WHITE);
        box.setForeground(Color.BLACK);
        box.setFont(new Font("SansSerif", Font.PLAIN, 14));
    }

    public void refreshTable() {
        loadAllCustomers();
    }
}