package com.bank;

import com.bank.service.BankService;
import com.bank.ui.LoginFrame;

import javax.swing.*;

/**
 * Application entry point. Loads any previously persisted data, applies
 * the system look-and-feel, and launches the login screen.
 */
public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fall back to the default cross-platform look and feel.
        }

        BankService.getInstance().load();

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
