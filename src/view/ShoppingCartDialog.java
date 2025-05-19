/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import javax.swing.*;

public class ShoppingCartDialog extends JDialog {
    public ShoppingCartDialog(JFrame parent) {
        super(parent, "Shopping Cart", true);
        setSize(300, 200);
        setLocationRelativeTo(parent);
        add(new JLabel("Shopping cart placeholder", SwingConstants.CENTER));
        setVisible(true);
    }
}

