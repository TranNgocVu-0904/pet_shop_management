package view;

import controller.PetController;
import model.Cat;
import model.Dog;
import model.Pet;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class PetFormDialog extends JDialog {
    private final JTextField nameField = new JTextField();
    private final JComboBox<String> typeBox = new JComboBox<>(new String[]{"DOG", "CAT"});
    private final JTextField breedField = new JTextField();
    private final JTextField ageField = new JTextField();
    private final JTextField priceField = new JTextField();
    private final PetPanel parent;
    private final Pet existingPet;

    public PetFormDialog(PetPanel parent, Pet pet) {
        super((Frame) null, true);
        this.parent = parent;
        this.existingPet = pet;

        setTitle(pet == null ? "Add Pet" : "Update Pet");
        setSize(400, 350);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(6, 2, 10, 10));

        // Form components
        add(new JLabel("Name:")); add(nameField);
        add(new JLabel("Type:")); add(typeBox);
        add(new JLabel("Breed:")); add(breedField);
        add(new JLabel("Age:")); add(ageField);
        add(new JLabel("Price:")); add(priceField);
        add(new JLabel()); // spacer

        JButton saveBtn = new JButton("Save");
        saveBtn.setBackground(new Color(0x007BFF));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        add(saveBtn);

        // If we're editing, fill in current values
        if (pet != null) {
            nameField.setText(pet.getName());
            breedField.setText(pet.getBreed());
            ageField.setText(String.valueOf(pet.getAge()));
            priceField.setText(pet.getPrice().toPlainString());
            typeBox.setSelectedItem(pet.getClass().getSimpleName().toUpperCase());
        }

        // Save action
        saveBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String type = (String) typeBox.getSelectedItem();
                String breed = breedField.getText().trim();
                int age = Integer.parseInt(ageField.getText().trim());
                BigDecimal price = new BigDecimal(priceField.getText().trim());

                Pet petToSave = switch (type) {
                    case "DOG" -> new Dog(name, breed, age, price);
                    case "CAT" -> new Cat(name, breed, age, price);
                    default -> throw new IllegalArgumentException("Unsupported pet type.");
                };

                if (pet != null) {
                    petToSave.setId(pet.getId());
                    PetController.updatePet(petToSave);
                } else {
                    PetController.addPet(petToSave, type);
                }

                parent.refreshTable();
                dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error: " + ex.getMessage(), "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        setVisible(true);
    }
}
