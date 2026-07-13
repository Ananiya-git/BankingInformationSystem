package com.bank.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a registered user of the banking system.
 * A user logs in with a username/password and may own one or more accounts.
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String username;
    private String passwordHash;
    private String fullName;
    private String address;
    private String phone;
    private String email;
    private final List<String> accountNumbers;

    public User(String username, String passwordHash, String fullName,
                String address, String phone, String email) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.accountNumbers = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getAccountNumbers() {
        return accountNumbers;
    }

    public void addAccountNumber(String accountNumber) {
        accountNumbers.add(accountNumber);
    }
}
