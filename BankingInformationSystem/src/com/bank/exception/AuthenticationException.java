package com.bank.exception;

/** Thrown when a login attempt fails or a username is already taken during registration. */
public class AuthenticationException extends Exception {
    public AuthenticationException(String message) {
        super(message);
    }
}
