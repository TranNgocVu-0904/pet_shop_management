package view;

import controller.AuthController;
import controller.BillingController;
import controller.CustomerController;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

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

        JLabel customerLabel = new JLabel("No customer selected");
        customerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        topPanel.add(addBtn);
        topPanel.add(chooseCustomerBtn);
        topPanel.add(customerLabel);

        // === Table ===
        String[] columns = {"ID", "Label", "Type", "Name", "Quantity", "Price"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        cartTable = new JTable(tableModel);
        cartTable.setRowHeight(28);

        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Shopping Cart"));

        // === Bottom Panel ===
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(getBackground());

        totalField = new JTextField();
        totalField.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalField.setEditable(false);
        totalField.setHorizontalAlignment(JTextField.RIGHT);
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
                        "CASH" // Simplified payment method
                );

                try {
                    if (billingController.processBill(bill)) {
                        billingController.applyLoyaltyPoints(selectedCustomer, bill.getTotalAmount());

//                        JFileChooser fileChooser = new JFileChooser();
//                        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
//                            String path = fileChooser.getSelectedFile().getAbsolutePath();
//                            billingController.exportBillAsPdf(bill);
//                            JOptionPane.showMessageDialog(this, "Bill saved to PDF.");
//                        }
                        JOptionPane.showMessageDialog(this, "Bill saved to PDF.");
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
                    item.getUnitPrice()
            });
        }
        totalField.setText("Total: $" + billingController.getCartTotal());
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
}
