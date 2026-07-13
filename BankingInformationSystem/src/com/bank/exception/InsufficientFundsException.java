package com.bank.exception;

/** Thrown when a withdrawal or transfer is attempted for more than the available balance. */
public class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
