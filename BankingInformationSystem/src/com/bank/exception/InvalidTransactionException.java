package com.bank.exception;

/** Thrown when a requested operation is malformed, e.g. a negative or zero amount. */
public class InvalidTransactionException extends Exception {
    public InvalidTransactionException(String message) {
        super(message);
    }
}
