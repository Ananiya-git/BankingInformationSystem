package com.bank.ui;

import com.bank.exception.AuthenticationException;
import com.bank.exception.InvalidTransactionException;
import com.bank.model.User;
import com.bank.service.BankService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Registration screen implementing requirement #1 (User Registration):
 * collects personal details and an initial deposit, then creates the
 * user's account and shows the newly generated account number.
 */
public class RegisterFrame extends JFrame {

    private final BankService bankService = BankService.getInstance();
    private final LoginFrame parent;

    private JTextField nameField;
    private JTextField addressField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField initialDepositField;

    public RegisterFrame(LoginFrame parent) {
        super("Banking Information System - New Account Registration");
        this.parent = parent;
        buildUI();
    }

    private void buildUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(480, 560);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("Create Your Account", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(8, 2, 8, 10));
        nameField = new JTextField();
        addressField = new JTextField();
        phoneField = new JTextField();
        emailField = new JTextField();
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();
        initialDepositField = new JTextField("0.00");

        form.add(new JLabel("Full Name:"));
        form.add(nameField);
        form.add(new JLabel("Address:"));
        form.add(addressField);
        form.add(new JLabel("Phone:"));
        form.add(phoneField);
        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("Choose Username:"));
        form.add(usernameField);
        form.add(new JLabel("Choose Password:"));
        form.add(passwordField);
        form.add(new JLabel("Confirm Password:"));
        form.add(confirmPasswordField);
        form.add(new JLabel("Initial Deposit:"));
        form.add(initialDepositField);

        root.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 10, 0));
        JButton registerBtn = new JButton("Register");
        JButton backBtn = new JButton("Back to Login");
        registerBtn.addActionListener(this::onRegister);
        backBtn.addActionListener(this::onBack);
        buttons.add(registerBtn);
        buttons.add(backBtn);
        root.add(buttons, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void onRegister(ActionEvent e) {
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());

        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double initialDeposit;
        try {
            initialDeposit = Double.parseDouble(initialDepositField.getText().trim());
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Initial deposit must be a valid number.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            User user = bankService.registerUser(username, password, name, address, phone, email, initialDeposit);
            String accountNumber = user.getAccountNumbers().get(0);
            JOptionPane.showMessageDialog(this,
                    "Registration successful!\n\n" +
                            "Your account number is: " + accountNumber + "\n" +
                            "Opening balance: " + String.format("%.2f", initialDeposit) + "\n\n" +
                            "You can now log in with your username and password.",
                    "Registration Complete", JOptionPane.INFORMATION_MESSAGE);
            onBack(e);
        } catch (AuthenticationException | InvalidTransactionException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Registration Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onBack(ActionEvent e) {
        parent.setVisible(true);
        dispose();
    }
}
