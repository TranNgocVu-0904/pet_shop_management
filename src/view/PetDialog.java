package view;

import model.Dog;
import model.Cat;
import model.Pet;
import controller.PetController;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class PetDialog extends JDialog {
    private final JTextField nameField = new JTextField();
    private final JComboBox<String> typeBox = new JComboBox<>(new String[]{"DOG", "CAT"});
    private final JTextField breedField = new JTextField();
    private final JTextField ageField = new JTextField();
    private final JTextField priceField = new JTextField();
    private final PetPanel parent;
    private final Pet existingPet;

    public PetDialog(PetPanel parent, Pet pet) {
        super((Frame) null, true);
        this.parent = parent;
        this.existingPet = pet;

        setTitle(pet == null ? "Add Pet" : "Update Pet");
        setSize(400, 350);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(6, 2, 10, 10));

        add(new JLabel("Name:")); add(nameField);
        add(new JLabel("Type:")); add(typeBox);
        add(new JLabel("Breed:")); add(breedField);
        add(new JLabel("Age:")); add(ageField);
        add(new JLabel("Price:")); add(priceField);
        add(new JLabel()); 

        JButton saveBtn = new JButton("Save");
        add(saveBtn);

        if (pet != null) {
            nameField.setText(pet.getName());
            breedField.setText(pet.getBreed());
            ageField.setText(String.valueOf(pet.getAge()));
            priceField.setText(pet.getPrice().toPlainString());
            typeBox.setSelectedItem(pet.getClass().getSimpleName().toUpperCase());
        }

        saveBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String breed = breedField.getText().trim();
                int age = Integer.parseInt(ageField.getText().trim());
                BigDecimal price = new BigDecimal(priceField.getText().trim());
                String type = typeBox.getSelectedItem().toString();

                Pet petToSave = switch (type) {
                    case "DOG" -> new Dog(name, breed, age, price);
                    case "CAT" -> new Cat(name, breed, age, price);
                    default -> throw new IllegalArgumentException("Unsupported pet type");
                };

                if (existingPet != null) {
                    petToSave.setId(existingPet.getId());
                    PetController.updatePet(petToSave);
                } else {
                    PetController.addPet(petToSave, type);
                }

                parent.refreshTable();
                dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        setVisible(true);
    }
}
