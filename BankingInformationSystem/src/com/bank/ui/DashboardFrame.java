package com.bank.ui;

import com.bank.model.Account;
import com.bank.model.User;
import com.bank.service.BankService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Main hub shown after a successful login. Lists the user's accounts and
 * provides access to every other feature: deposit/withdraw, transfer,
 * statements, and account/profile management.
 */
public class DashboardFrame extends JFrame {

    private final BankService bankService = BankService.getInstance();
    private final User user;

    private JTable accountsTable;
    private DefaultTableModel tableModel;
    private JLabel welcomeLabel;

    public DashboardFrame(User user) {
        super("Banking Information System - Dashboard");
        this.user = user;
        buildUI();
        refreshAccounts();
    }

    private void buildUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 480);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        welcomeLabel = new JLabel();
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        root.add(welcomeLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"Account Number", "Holder Name", "Balance"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        accountsTable = new JTable(tableModel);
        accountsTable.setFillsViewportHeight(true);
        root.add(new JScrollPane(accountsTable), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(3, 3, 10, 10));
        JButton depositBtn = new JButton("Deposit / Withdraw");
        JButton transferBtn = new JButton("Transfer Funds");
        JButton statementBtn = new JButton("View Statement");
        JButton newAccountBtn = new JButton("Open New Account");
        JButton profileBtn = new JButton("Manage Profile");
        JButton refreshBtn = new JButton("Refresh");
        JButton logoutBtn = new JButton("Logout");

        depositBtn.addActionListener(this::onDepositWithdraw);
        transferBtn.addActionListener(this::onTransfer);
        statementBtn.addActionListener(this::onStatement);
        newAccountBtn.addActionListener(this::onNewAccount);
        profileBtn.addActionListener(this::onProfile);
        refreshBtn.addActionListener(e -> refreshAccounts());
        logoutBtn.addActionListener(this::onLogout);

        buttons.add(depositBtn);
        buttons.add(transferBtn);
        buttons.add(statementBtn);
        buttons.add(newAccountBtn);
        buttons.add(profileBtn);
        buttons.add(refreshBtn);
        buttons.add(logoutBtn);

        root.add(buttons, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private void refreshAccounts() {
        welcomeLabel.setText("Welcome, " + user.getFullName() + "   (username: " + user.getUsername() + ")");
        tableModel.setRowCount(0);
        List<Account> accounts = bankService.getAccountsForUser(user.getUsername());
        for (Account acc : accounts) {
            tableModel.addRow(new Object[]{
                    acc.getAccountNumber(),
                    acc.getHolderName(),
                    String.format("%.2f", acc.getBalance())
            });
        }
    }

    /** Returns the account number currently selected in the table, or null if none. */
    private String getSelectedAccountNumber() {
        int row = accountsTable.getSelectedRow();
        if (row < 0) {
            return null;
        }
        return (String) tableModel.getValueAt(row, 0);
    }

    private void onDepositWithdraw(ActionEvent e) {
        List<Account> accounts = bankService.getAccountsForUser(user.getUsername());
        if (accounts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You have no accounts yet.", "No Accounts", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String preselected = getSelectedAccountNumber();
        DepositWithdrawDialog dialog = new DepositWithdrawDialog(this, accounts, preselected);
        dialog.setVisible(true);
        refreshAccounts();
    }

    private void onTransfer(ActionEvent e) {
        List<Account> accounts = bankService.getAccountsForUser(user.getUsername());
        if (accounts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You have no accounts yet.", "No Accounts", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String preselected = getSelectedAccountNumber();
        TransferDialog dialog = new TransferDialog(this, accounts, preselected);
        dialog.setVisible(true);
        refreshAccounts();
    }

    private void onStatement(ActionEvent e) {
        List<Account> accounts = bankService.getAccountsForUser(user.getUsername());
        if (accounts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You have no accounts yet.", "No Accounts", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String preselected = getSelectedAccountNumber();
        StatementDialog dialog = new StatementDialog(this, accounts, preselected);
        dialog.setVisible(true);
    }

    private void onNewAccount(ActionEvent e) {
        String input = JOptionPane.showInputDialog(this,
                "Opening deposit for the new account:", "0.00");
        if (input == null) {
            return;
        }
        try {
            double amount = Double.parseDouble(input.trim());
            bankService.openAdditionalAccount(user.getUsername(), amount);
            JOptionPane.showMessageDialog(this, "New account opened successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshAccounts();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onProfile(ActionEvent e) {
        AccountManagementDialog dialog = new AccountManagementDialog(this, user);
        dialog.setVisible(true);
        refreshAccounts();
    }

    private void onLogout(ActionEvent e) {
        new LoginFrame().setVisible(true);
        dispose();
    }
}
