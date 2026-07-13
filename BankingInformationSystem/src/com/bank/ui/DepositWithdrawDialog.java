package com.bank.ui;

import com.bank.model.Account;
import com.bank.service.BankService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Dialog implementing requirement #3 (Deposit and Withdrawal):
 * lets the user pick an account, choose deposit or withdrawal, enter
 * an amount, and shows a confirmation with the resulting balance.
 */
public class DepositWithdrawDialog extends JDialog {

    private final BankService bankService = BankService.getInstance();
    private final List<Account> accounts;

    private JComboBox<String> accountCombo;
    private JRadioButton depositRadio;
    private JRadioButton withdrawRadio;
    private JTextField amountField;

    public DepositWithdrawDialog(Frame owner, List<Account> accounts, String preselectedAccount) {
        super(owner, "Deposit / Withdraw", true);
        this.accounts = accounts;
        buildUI(preselectedAccount);
    }

    private void buildUI(String preselectedAccount) {
        setSize(380, 280);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 12));
        accountCombo = new JComboBox<>();
        for (Account acc : accounts) {
            accountCombo.addItem(acc.getAccountNumber() + "  (Balance: " + String.format("%.2f", acc.getBalance()) + ")");
        }
        if (preselectedAccount != null) {
            for (int i = 0; i < accounts.size(); i++) {
                if (accounts.get(i).getAccountNumber().equals(preselectedAccount)) {
                    accountCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
        form.add(new JLabel("Account:"));
        form.add(accountCombo);

        depositRadio = new JRadioButton("Deposit", true);
        withdrawRadio = new JRadioButton("Withdraw");
        ButtonGroup group = new ButtonGroup();
        group.add(depositRadio);
        group.add(withdrawRadio);
        JPanel radioPanel = new JPanel(new GridLayout(1, 2));
        radioPanel.add(depositRadio);
        radioPanel.add(withdrawRadio);
        form.add(new JLabel("Transaction Type:"));
        form.add(radioPanel);

        amountField = new JTextField();
        form.add(new JLabel("Amount:"));
        form.add(amountField);

        root.add(form, BorderLayout.CENTER);

        JButton submitBtn = new JButton("Submit");
        submitBtn.addActionListener(this::onSubmit);
        root.add(submitBtn, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void onSubmit(ActionEvent e) {
        int index = accountCombo.getSelectedIndex();
        if (index < 0) {
            return;
        }
        Account account = accounts.get(index);
        double amount;
        try {
            amount = Double.parseDouble(amountField.getText().trim());
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (depositRadio.isSelected()) {
                double newBalance = bankService.deposit(account.getAccountNumber(), amount);
                JOptionPane.showMessageDialog(this,
                        "Deposit successful!\n\nAmount deposited: " + String.format("%.2f", amount) +
                                "\nNew balance: " + String.format("%.2f", newBalance),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                double newBalance = bankService.withdraw(account.getAccountNumber(), amount);
                JOptionPane.showMessageDialog(this,
                        "Withdrawal successful!\n\nAmount withdrawn: " + String.format("%.2f", amount) +
                                "\nNew balance: " + String.format("%.2f", newBalance),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Transaction Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
