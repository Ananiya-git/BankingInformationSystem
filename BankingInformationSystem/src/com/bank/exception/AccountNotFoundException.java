package com.bank.exception;

/** Thrown when a referenced account number does not exist in the system. */
public class AccountNotFoundException extends Exception {
    public AccountNotFoundException(String message) {
        super(message);
    }
}
