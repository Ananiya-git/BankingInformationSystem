package com.bank.ui;

import com.bank.model.Account;
import com.bank.service.BankService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Dialog implementing requirement #4 (Fund Transfer): user chooses a
 * source account they own, types the recipient's account number and an
 * amount, and receives confirmation with both updated balances.
 */
public class TransferDialog extends JDialog {

    private final BankService bankService = BankService.getInstance();
    private final List<Account> accounts;

    private JComboBox<String> fromAccountCombo;
    private JTextField toAccountField;
    private JTextField amountField;

    public TransferDialog(Frame owner, List<Account> accounts, String preselectedAccount) {
        super(owner, "Transfer Funds", true);
        this.accounts = accounts;
        buildUI(preselectedAccount);
    }

    private void buildUI(String preselectedAccount) {
        setSize(400, 260);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 12));
        fromAccountCombo = new JComboBox<>();
        for (Account acc : accounts) {
            fromAccountCombo.addItem(acc.getAccountNumber() + "  (Balance: " + String.format("%.2f", acc.getBalance()) + ")");
        }
        if (preselectedAccount != null) {
            for (int i = 0; i < accounts.size(); i++) {
                if (accounts.get(i).getAccountNumber().equals(preselectedAccount)) {
                    fromAccountCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
        form.add(new JLabel("From Account:"));
        form.add(fromAccountCombo);

        toAccountField = new JTextField();
        form.add(new JLabel("To Account Number:"));
        form.add(toAccountField);

        amountField = new JTextField();
        form.add(new JLabel("Amount:"));
        form.add(amountField);

        root.add(form, BorderLayout.CENTER);

        JButton submitBtn = new JButton("Transfer");
        submitBtn.addActionListener(this::onSubmit);
        root.add(submitBtn, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void onSubmit(ActionEvent e) {
        int index = fromAccountCombo.getSelectedIndex();
        if (index < 0) {
            return;
        }
        Account from = accounts.get(index);
        String toAccountNumber = toAccountField.getText().trim();
        if (toAccountNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a recipient account number.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amountField.getText().trim());
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            BankService.TransferResult result = bankService.transfer(from.getAccountNumber(), toAccountNumber, amount);
            JOptionPane.showMessageDialog(this,
                    "Transfer successful!\n\n" +
                            "Amount transferred: " + String.format("%.2f", amount) + "\n" +
                            "Your new balance (" + from.getAccountNumber() + "): " +
                            String.format("%.2f", result.senderBalance) + "\n" +
                            "Recipient's new balance (" + toAccountNumber + "): " +
                            String.format("%.2f", result.recipientBalance),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Transfer Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
