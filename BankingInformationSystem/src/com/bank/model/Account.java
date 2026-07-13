package com.bank.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a single bank account belonging to a user.
 * A user may hold more than one account.
 */
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String accountNumber;
    private final String ownerUsername;
    private String holderName;
    private double balance;
    private final List<Transaction> transactions;

    public Account(String accountNumber, String ownerUsername, String holderName, double openingBalance) {
        this.accountNumber = accountNumber;
        this.ownerUsername = ownerUsername;
        this.holderName = holderName;
        this.balance = openingBalance;
        this.transactions = new ArrayList<>();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public double getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    /** Adjusts the balance upward and records a matching transaction entry. */
    public Transaction credit(String transactionId, double amount, Transaction.Type type, String description) {
        this.balance += amount;
        Transaction t = new Transaction(transactionId, type, amount, this.balance, description);
        transactions.add(t);
        return t;
    }

    /** Adjusts the balance downward and records a matching transaction entry. */
    public Transaction debit(String transactionId, double amount, Transaction.Type type, String description) {
        this.balance -= amount;
        Transaction t = new Transaction(transactionId, type, amount, this.balance, description);
        transactions.add(t);
        return t;
    }
}
