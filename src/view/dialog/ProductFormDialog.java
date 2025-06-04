package view.dialog;

import view.panel.ProductPanel;
import model.product.Food;
import model.product.Toy;
import model.product.Medicine;
import model.product.Product;
import controller.product.ProductController;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ProductFormDialog extends JDialog {
    private final JTextField nameField = new JTextField();
    private final JTextField priceField = new JTextField();
    private final JTextField quantityField = new JTextField();
    private final JComboBox<String> categoryBox = new JComboBox<>(new String[]{"TOY", "FOOD", "MEDICINE"});

    // Type-specific fields
    private final JTextField materialField = new JTextField(); // TOY
    private final JTextField expirationField = new JTextField(); // FOOD + MEDICINE
    private final JTextField nutritionalField = new JTextField(); // FOOD
    private final JTextField manufactureField = new JTextField(); // MEDICINE
    private final JTextField dosageField = new JTextField(); // MEDICINE

    private final ProductPanel parent;
    private final Product existing;

    public ProductFormDialog(ProductPanel parent, Product product) {
        super((Frame) null, true);
        this.parent = parent;
        this.existing = product;

        setTitle(product == null ? "Add Product" : "Update Product");
        setSize(500, 500);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(0, 2, 10, 10));

        add(new JLabel("Name:")); add(nameField);
        add(new JLabel("Price:")); add(priceField);
        add(new JLabel("Stock Quantity:")); add(quantityField);
        add(new JLabel("Category:")); add(categoryBox);

        add(new JLabel("Material:")); add(materialField);
        add(new JLabel("Expiration Date (YYYY-MM-DD):")); add(expirationField);
        add(new JLabel("Nutritional Info:")); add(nutritionalField);
        add(new JLabel("Manufacture Date (YYYY-MM-DD):")); add(manufactureField);
        add(new JLabel("Dosage:")); add(dosageField);

        JButton saveBtn = new JButton("Save");
        add(new JLabel());
        add(saveBtn);

        // Pre-fill data
        if (product != null) {
            nameField.setText(product.getName());
            priceField.setText(product.getPrice().toPlainString());
            quantityField.setText(String.valueOf(product.getStockQuantity()));
            categoryBox.setSelectedItem(product.getClass().getSimpleName().toUpperCase());

            if (product instanceof Toy t) {
                materialField.setText(t.getMaterial());
            } else if (product instanceof Food f) {
                expirationField.setText(f.getExpirationDate().toString());
                nutritionalField.setText(f.getNutritionalInfo());
            } else if (product instanceof Medicine m) {
                expirationField.setText(m.getExpirationDate().toString());
                manufactureField.setText(m.getManufactureDate().toString());
                dosageField.setText(m.getDosage());
            }
        }

        categoryBox.addActionListener(e -> toggleFields());

        saveBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                BigDecimal price = new BigDecimal(priceField.getText().trim());
                int quantity = Integer.parseInt(quantityField.getText().trim());
                String type = categoryBox.getSelectedItem().toString();

                Product productToSave = switch (type) {
                    case "TOY" -> new Toy(name, price, quantity, materialField.getText().trim());
                    case "FOOD" -> new Food(name, price, quantity,
                            LocalDate.parse(expirationField.getText().trim()),
                            nutritionalField.getText().trim());
                    case "MEDICINE" -> new Medicine(name, price, quantity,
                            dosageField.getText().trim(),
                            LocalDate.parse(manufactureField.getText().trim()),
                            LocalDate.parse(expirationField.getText().trim()));
                    default -> throw new IllegalArgumentException("Unknown product type");
                };

                if (existing != null) productToSave.setId(existing.getId());

                if (existing == null) {
                    ProductController.addProduct(productToSave);
                } else {
                    ProductController.updateProduct(productToSave);
                }

                parent.refreshTable();
                dispose();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        toggleFields(); // Initialize correct visibility
        setVisible(true);
    }

    private void toggleFields() {
        String type = categoryBox.getSelectedItem().toString();

        materialField.setEnabled(type.equals("TOY"));
        expirationField.setEnabled(type.equals("FOOD") || type.equals("MEDICINE"));
        nutritionalField.setEnabled(type.equals("FOOD"));
        manufactureField.setEnabled(type.equals("MEDICINE"));
        dosageField.setEnabled(type.equals("MEDICINE"));
    }
}
