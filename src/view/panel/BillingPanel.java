package view.panel;

import util.ui.ButtonCellRenderer;
import util.ui.ButtonCellEditor;
import model.billing.BillItem;
import model.billing.Bill;
import model.user.Customer;
import controller.user.AuthController;
import controller.bill.BillingController;
import controller.customer.CustomerController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import view.dialog.AddItemDialog;

public class BillingPanel extends JPanel {
    private final JTable cartTable;
    private final DefaultTableModel tableModel;
    private final JTextField totalField;
    private final BillingController billingController = new BillingController();
    private Customer selectedCustomer;

    public BillingPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 236, 236));
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // === Top Panel ===
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(getBackground());

        JButton addBtn = createRoundedButton("Add Item");
        
        JButton chooseCustomerBtn = createRoundedButton("Choose Customer");
        chooseCustomerBtn.setPreferredSize(new Dimension(160, 30));

        JLabel customerLabel = new JLabel("No customer selected");
        customerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        topPanel.add(addBtn);
        topPanel.add(chooseCustomerBtn);
        topPanel.add(customerLabel);

        // === Table ===
        String[] columns = {"ID", "Label", "Type", "Name", "Quantity", "Price", "Update", "Delete"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return col == 6 || col == 7;
            }
        };
        cartTable = new JTable(tableModel);
        cartTable.setRowHeight(28);

        // Add buttons to table
        cartTable.getColumn("Update").setCellRenderer(new ButtonCellRenderer("✏️"));
        cartTable.getColumn("Delete").setCellRenderer(new ButtonCellRenderer("🗑️"));

        cartTable.getColumn("Update").setCellEditor(new ButtonCellEditor<>(
                cartTable,
                "update",
                this::mapRowToItem,
                this::handleUpdate,
                null
        ));

        cartTable.getColumn("Delete").setCellEditor(new ButtonCellEditor<>(
                cartTable,
                "delete",
                this::mapRowToItem,
                null,
                this::handleDelete
        ));

        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Shopping Cart"));

        // === Bottom Panel ===
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(getBackground());

        totalField = new JTextField();
        totalField.setPreferredSize(new Dimension(1000, 35));
        totalField.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalField.setEditable(false);
        totalField.setHorizontalAlignment(JTextField.LEFT);
        totalField.setText("Total: $0.00");
        bottomPanel.add(totalField, BorderLayout.WEST);

        JButton printBtn = createRoundedButton("Print Bill 🧾");
        bottomPanel.add(printBtn, BorderLayout.EAST);

        // === Add Actions ===
        addBtn.addActionListener(e -> new AddItemDialog(this, billingController));
        chooseCustomerBtn.addActionListener(e -> {
            List<Customer> allCustomers = CustomerController.getAllCustomers();
            Customer selected = (Customer) JOptionPane.showInputDialog(
                    this,
                    "Select Customer:",
                    "Customer Picker",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    allCustomers.toArray(),
                    null
            );
            if (selected != null) {
                selectedCustomer = selected;
                customerLabel.setText("Customer: " + selected.getName());
            }
        });

        printBtn.addActionListener(e -> {
            if (selectedCustomer == null) {
                JOptionPane.showMessageDialog(this, "Please select a customer.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Print this bill?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Bill bill = billingController.finalizeBill(
                        selectedCustomer.getId(),
                        AuthController.currentUser.getId(),
                        "CASH"
                );

                try {
                    if (billingController.processBill(bill)) {
                        billingController.exportBillAsPdf(bill);
                        billingController.applyLoyaltyPoints(selectedCustomer, bill.getTotalAmount());
                        JOptionPane.showMessageDialog(this, "Bill saved.");
                        refreshCart();
                    } else {
                        JOptionPane.showMessageDialog(this, "Error processing bill.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        });

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshCart();
    }

    public void refreshCart() {
        tableModel.setRowCount(0);
        for (BillItem item : billingController.getCartItemsAsList()) {
            String label = item.getItemType().toString();
            String type = item.getType();

            tableModel.addRow(new Object[]{
                    item.getItemId(),
                    label,
                    type,
                    item.getItemName(),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    "✏️",
                    "🗑️"
            });
        }
        totalField.setText("Total: $" + billingController.getCartTotal());
    }

    private BillItem mapRowToItem(DefaultTableModel model, int row) {
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        String label = model.getValueAt(row, 1).toString();
        String type = model.getValueAt(row, 2).toString();
        String name = model.getValueAt(row, 3).toString();
        int quantity = Integer.parseInt(model.getValueAt(row, 4).toString());
        BigDecimal price = new BigDecimal(model.getValueAt(row, 5).toString());

        if ("PRODUCT".equals(label)) {
            return new BillItem(id, name, price, quantity, type); // productType = type
        } else {
            return new BillItem(id, name, price, type); // petType = type
        }
    }

    private void handleUpdate(BillItem item) {
        if (item.getItemType() == BillItem.ItemType.PRODUCT) {
            String input = JOptionPane.showInputDialog(this, "Enter new quantity:", item.getQuantity());
            try {
                int qty = Integer.parseInt(input);
                if (qty > 0) {
                    billingController.updateCartItem(item.getProductId(), qty);
                    refreshCart();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid quantity.");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid input.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pets cannot be updated.");
        }
    }

    private void handleDelete(BillItem item) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Remove " + item.getItemName() + " from cart?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            billingController.removeCartItem(item.getItemId());
            refreshCart();
        }
    }

    private JButton createRoundedButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(120, 30)); // ⬅️ Set size here
        button.setBackground(new Color(0x007BFF));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14)); // ⬅️ Optional: Increase font
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

}
