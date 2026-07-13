package com.bank.ui;

import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.service.BankService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Dialog implementing requirement #5 (Account Statements): shows the
 * full transaction history for a selected account -- date, type,
 * amount and resulting balance.
 */
public class StatementDialog extends JDialog {

    private final BankService bankService = BankService.getInstance();
    private final List<Account> accounts;

    private JComboBox<String> accountCombo;
    private DefaultTableModel tableModel;

    public StatementDialog(Frame owner, List<Account> accounts, String preselectedAccount) {
        super(owner, "Account Statement", true);
        this.accounts = accounts;
        buildUI(preselectedAccount);
    }

    private void buildUI(String preselectedAccount) {
        setSize(650, 420);
        setLocationRelativeTo(getOwner());

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel top = new JPanel(new BorderLayout(10, 0));
        accountCombo = new JComboBox<>();
        for (Account acc : accounts) {
            accountCombo.addItem(acc.getAccountNumber());
        }
        top.add(new JLabel("Select Account:"), BorderLayout.WEST);
        top.add(accountCombo, BorderLayout.CENTER);
        root.add(top, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[]{"Date / Time", "Type", "Amount", "Balance After", "Description"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        accountCombo.addActionListener(e -> loadStatement());

        if (preselectedAccount != null) {
            for (int i = 0; i < accounts.size(); i++) {
                if (accounts.get(i).getAccountNumber().equals(preselectedAccount)) {
                    accountCombo.setSelectedIndex(i);
                    break;
                }
            }
        }

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        JPanel bottom = new JPanel();
        bottom.add(closeBtn);
        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);
        loadStatement();
    }

    private void loadStatement() {
        int index = accountCombo.getSelectedIndex();
        if (index < 0) {
            return;
        }
        String accountNumber = accounts.get(index).getAccountNumber();
        tableModel.setRowCount(0);
        try {
            List<Transaction> transactions = bankService.getStatement(accountNumber);
            for (Transaction t : transactions) {
                tableModel.addRow(new Object[]{
                        t.getFormattedDateTime(),
                        t.getType(),
                        String.format("%.2f", t.getAmount()),
                        String.format("%.2f", t.getBalanceAfter()),
                        t.getDescription()
                });
            }
            if (transactions.isEmpty()) {
                tableModel.addRow(new Object[]{"-", "-", "-", "-", "No transactions yet."});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
