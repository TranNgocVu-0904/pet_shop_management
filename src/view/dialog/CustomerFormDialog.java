package view.dialog;

import view.panel.CustomerPanel;
import controller.customer.CustomerController;
import model.user.Customer;

import javax.swing.*;
import java.awt.*;

public class CustomerFormDialog extends JDialog {
    private final JTextField nameField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JTextField phoneField = new JTextField(20);
    private final JTextField loyaltyField = new JTextField(5);

    private final CustomerPanel parent;
    private final Customer existingCustomer;

    public CustomerFormDialog(CustomerPanel parent, Customer customer) {
        super((Frame) SwingUtilities.getWindowAncestor(parent), true);
        this.parent = parent;
        this.existingCustomer = customer;

        setTitle(customer == null ? "Add Customer" : "Update Customer");
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        form.add(new JLabel("Name:"));
        form.add(nameField);

        form.add(new JLabel("Email:"));
        form.add(emailField);

        form.add(new JLabel("Phone:"));
        form.add(phoneField);

        form.add(new JLabel("Loyalty Points:"));
        form.add(loyaltyField);

        if (customer != null) {
            nameField.setText(customer.getName());
            emailField.setText(customer.getEmail());
            phoneField.setText(customer.getPhone());
            loyaltyField.setText(String.valueOf(customer.getLoyaltyPoints()));
        } else {
            loyaltyField.setText("0");
        }

        JButton saveBtn = new JButton(customer == null ? "Create" : "Update");
        JButton cancelBtn = new JButton("Cancel");

        JPanel btnPanel = new JPanel();
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        saveBtn.addActionListener(e -> saveCustomer());
        cancelBtn.addActionListener(e -> dispose());

        add(form, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void saveCustomer() {
        try {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            int loyalty = Integer.parseInt(loyaltyField.getText().trim());

            if (loyalty < 0) {
                JOptionPane.showMessageDialog(this, "Loyalty points cannot be negative.");
                return;
            }

            if (existingCustomer != null) {
                existingCustomer.setName(name);
                existingCustomer.setEmail(email);
                existingCustomer.setPhone(phone);
                existingCustomer.setLoyaltyPoints(loyalty); 
                CustomerController.updateCustomer(existingCustomer);
            } else {
                Customer c = new Customer(name, email, phone);
                c.setLoyaltyPoints(loyalty); 
                CustomerController.addCustomer(c);
            }

            parent.refreshTable();
            dispose();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Loyalty points must be a valid number.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
