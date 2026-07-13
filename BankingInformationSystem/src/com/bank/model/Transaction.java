package com.bank.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single transaction (deposit, withdrawal, or transfer leg)
 * recorded against an account.
 */
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public enum Type {
        DEPOSIT, WITHDRAWAL, TRANSFER_OUT, TRANSFER_IN
    }

    private final String transactionId;
    private final LocalDateTime dateTime;
    private final Type type;
    private final double amount;
    private final double balanceAfter;
    private final String description;

    public Transaction(String transactionId, Type type, double amount,
                        double balanceAfter, String description) {
        this.transactionId = transactionId;
        this.dateTime = LocalDateTime.now();
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getFormattedDateTime() {
        return dateTime.format(FORMATTER);
    }

    public Type getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("[%s] %-13s Amount: %10.2f  Balance: %10.2f  %s",
                getFormattedDateTime(), type, amount, balanceAfter, description);
    }
}
