package com.bank.ui;

import com.bank.model.User;
import com.bank.service.BankService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Dialog implementing requirement #2 (Account Management): lets the
 * user view and update their personal details, and optionally change
 * their password.
 */
public class AccountManagementDialog extends JDialog {

    private final BankService bankService = BankService.getInstance();
    private final User user;

    private JTextField nameField;
    private JTextField addressField;
    private JTextField phoneField;
    private JTextField emailField;
    private JPasswordField newPasswordField;

    public AccountManagementDialog(Frame owner, User user) {
        super(owner, "Manage Profile", true);
        this.user = user;
        buildUI();
    }

    private void buildUI() {
        setSize(420, 340);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 12));
        nameField = new JTextField(user.getFullName());
        addressField = new JTextField(user.getAddress());
        phoneField = new JTextField(user.getPhone());
        emailField = new JTextField(user.getEmail());
        newPasswordField = new JPasswordField();

        form.add(new JLabel("Full Name:"));
        form.add(nameField);
        form.add(new JLabel("Address:"));
        form.add(addressField);
        form.add(new JLabel("Phone:"));
        form.add(phoneField);
        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("New Password (optional):"));
        form.add(newPasswordField);

        root.add(form, BorderLayout.CENTER);

        JButton saveBtn = new JButton("Save Changes");
        saveBtn.addActionListener(this::onSave);
        root.add(saveBtn, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void onSave(ActionEvent e) {
        try {
            bankService.updateUserDetails(user.getUsername(), nameField.getText().trim(),
                    addressField.getText().trim(), phoneField.getText().trim(), emailField.getText().trim());

            char[] pwd = newPasswordField.getPassword();
            if (pwd.length > 0) {
                bankService.changePassword(user.getUsername(), new String(pwd));
            }

            JOptionPane.showMessageDialog(this, "Account information has been successfully updated.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Update Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
