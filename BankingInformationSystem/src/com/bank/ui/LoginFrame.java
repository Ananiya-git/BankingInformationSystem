package com.bank.ui;

import com.bank.exception.AuthenticationException;
import com.bank.model.User;
import com.bank.service.BankService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Login screen. Provides access to registration and, on successful
 * authentication, launches the Dashboard.
 */
public class LoginFrame extends JFrame {

    private final BankService bankService = BankService.getInstance();

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        super("Banking Information System - Login");
        buildUI();
    }

    private void buildUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 320);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("Welcome to SimBank", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(2, 2, 8, 12));
        form.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        form.add(new JLabel("Username:"));
        form.add(usernameField);
        form.add(new JLabel("Password:"));
        form.add(passwordField);
        root.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(2, 1, 0, 10));
        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Create New Account");
        loginBtn.addActionListener(this::onLogin);
        registerBtn.addActionListener(this::onRegister);
        buttons.add(loginBtn);
        buttons.add(registerBtn);
        root.add(buttons, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(loginBtn);
        setContentPane(root);
    }

    private void onLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        try {
            User user = bankService.login(username, password);
            JOptionPane.showMessageDialog(this,
                    "Login successful. Welcome, " + user.getFullName() + "!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            new DashboardFrame(user).setVisible(true);
            dispose();
        } catch (AuthenticationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRegister(ActionEvent e) {
        new RegisterFrame(this).setVisible(true);
        setVisible(false);
    }
}
