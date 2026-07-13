package com.bank.service;

import com.bank.exception.AccountNotFoundException;
import com.bank.exception.AuthenticationException;
import com.bank.exception.InsufficientFundsException;
import com.bank.exception.InvalidTransactionException;
import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.model.User;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Central service class that implements all banking business logic:
 * registration, authentication, account management, deposits, withdrawals,
 * fund transfers and statement generation. Also responsible for persisting
 * all data to disk between sessions.
 *
 * Implemented as a singleton so every UI screen shares the same in-memory
 * state.
 */
public class BankService {

    private static final String DATA_DIR = "data";
    private static final String DATA_FILE = DATA_DIR + File.separator + "bank_data.ser";

    private static BankService instance;

    private final Map<String, User> usersByUsername;
    private final Map<String, Account> accountsByNumber;
    private int accountNumberSeq;
    private int transactionSeq;

    private BankService() {
        this.usersByUsername = new HashMap<>();
        this.accountsByNumber = new HashMap<>();
        this.accountNumberSeq = 100001;
        this.transactionSeq = 1;
    }

    public static synchronized BankService getInstance() {
        if (instance == null) {
            instance = new BankService();
        }
        return instance;
    }

    // ----------------------------------------------------------------
    // Registration & authentication
    // ----------------------------------------------------------------

    public synchronized User registerUser(String username, String password, String fullName,
                                           String address, String phone, String email,
                                           double initialDeposit) throws AuthenticationException,
                                                                          InvalidTransactionException {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidTransactionException("Username cannot be empty.");
        }
        if (password == null || password.length() < 4) {
            throw new InvalidTransactionException("Password must be at least 4 characters long.");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new InvalidTransactionException("Full name is required.");
        }
        if (usersByUsername.containsKey(username)) {
            throw new AuthenticationException("Username '" + username + "' is already taken.");
        }
        if (initialDeposit < 0) {
            throw new InvalidTransactionException("Initial deposit cannot be negative.");
        }

        User user = new User(username, hash(password), fullName, address, phone, email);
        usersByUsername.put(username, user);

        String accountNumber = "ACC" + (accountNumberSeq++);
        Account account = new Account(accountNumber, username, fullName, 0.0);
        if (initialDeposit > 0) {
            account.credit(nextTxnId(), initialDeposit, Transaction.Type.DEPOSIT, "Opening balance");
        }
        accountsByNumber.put(accountNumber, account);
        user.addAccountNumber(accountNumber);

        save();
        return user;
    }

    public synchronized User login(String username, String password) throws AuthenticationException {
        User user = usersByUsername.get(username);
        if (user == null || !user.getPasswordHash().equals(hash(password))) {
            throw new AuthenticationException("Invalid username or password.");
        }
        return user;
    }

    public synchronized void updateUserDetails(String username, String fullName, String address,
                                                String phone, String email) throws AccountNotFoundException {
        User user = usersByUsername.get(username);
        if (user == null) {
            throw new AccountNotFoundException("No such user: " + username);
        }
        user.setFullName(fullName);
        user.setAddress(address);
        user.setPhone(phone);
        user.setEmail(email);
        for (String accNum : user.getAccountNumbers()) {
            Account acc = accountsByNumber.get(accNum);
            if (acc != null) {
                acc.setHolderName(fullName);
            }
        }
        save();
    }

    public synchronized void changePassword(String username, String newPassword)
            throws AccountNotFoundException, InvalidTransactionException {
        User user = usersByUsername.get(username);
        if (user == null) {
            throw new AccountNotFoundException("No such user: " + username);
        }
        if (newPassword == null || newPassword.length() < 4) {
            throw new InvalidTransactionException("Password must be at least 4 characters long.");
        }
        user.setPasswordHash(hash(newPassword));
        save();
    }

    // ----------------------------------------------------------------
    // Account management
    // ----------------------------------------------------------------

    public synchronized List<Account> getAccountsForUser(String username) {
        List<Account> result = new ArrayList<>();
        User user = usersByUsername.get(username);
        if (user != null) {
            for (String accNum : user.getAccountNumbers()) {
                Account acc = accountsByNumber.get(accNum);
                if (acc != null) {
                    result.add(acc);
                }
            }
        }
        return result;
    }

    public synchronized Account openAdditionalAccount(String username, double initialDeposit)
            throws AccountNotFoundException, InvalidTransactionException {
        User user = usersByUsername.get(username);
        if (user == null) {
            throw new AccountNotFoundException("No such user: " + username);
        }
        if (initialDeposit < 0) {
            throw new InvalidTransactionException("Initial deposit cannot be negative.");
        }
        String accountNumber = "ACC" + (accountNumberSeq++);
        Account account = new Account(accountNumber, username, user.getFullName(), 0.0);
        if (initialDeposit > 0) {
            account.credit(nextTxnId(), initialDeposit, Transaction.Type.DEPOSIT, "Opening balance");
        }
        accountsByNumber.put(accountNumber, account);
        user.addAccountNumber(accountNumber);
        save();
        return account;
    }

    public synchronized Account getAccount(String accountNumber) throws AccountNotFoundException {
        Account acc = accountsByNumber.get(accountNumber);
        if (acc == null) {
            throw new AccountNotFoundException("Account not found: " + accountNumber);
        }
        return acc;
    }

    // ----------------------------------------------------------------
    // Deposit / Withdraw / Transfer
    // ----------------------------------------------------------------

    public synchronized double deposit(String accountNumber, double amount)
            throws AccountNotFoundException, InvalidTransactionException {
        if (amount <= 0) {
            throw new InvalidTransactionException("Deposit amount must be greater than zero.");
        }
        Account acc = getAccount(accountNumber);
        acc.credit(nextTxnId(), amount, Transaction.Type.DEPOSIT, "Cash deposit");
        save();
        return acc.getBalance();
    }

    public synchronized double withdraw(String accountNumber, double amount)
            throws AccountNotFoundException, InvalidTransactionException, InsufficientFundsException {
        if (amount <= 0) {
            throw new InvalidTransactionException("Withdrawal amount must be greater than zero.");
        }
        Account acc = getAccount(accountNumber);
        if (acc.getBalance() < amount) {
            throw new InsufficientFundsException(
                    "Insufficient funds. Available balance: " + String.format("%.2f", acc.getBalance()));
        }
        acc.debit(nextTxnId(), amount, Transaction.Type.WITHDRAWAL, "Cash withdrawal");
        save();
        return acc.getBalance();
    }

    /** Result holder describing the outcome of a fund transfer. */
    public static class TransferResult {
        public final double senderBalance;
        public final double recipientBalance;

        public TransferResult(double senderBalance, double recipientBalance) {
            this.senderBalance = senderBalance;
            this.recipientBalance = recipientBalance;
        }
    }

    public synchronized TransferResult transfer(String fromAccountNumber, String toAccountNumber, double amount)
            throws AccountNotFoundException, InvalidTransactionException, InsufficientFundsException {
        if (amount <= 0) {
            throw new InvalidTransactionException("Transfer amount must be greater than zero.");
        }
        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new InvalidTransactionException("Cannot transfer to the same account.");
        }
        Account from = getAccount(fromAccountNumber);
        Account to = getAccount(toAccountNumber);
        if (from.getBalance() < amount) {
            throw new InsufficientFundsException(
                    "Insufficient funds. Available balance: " + String.format("%.2f", from.getBalance()));
        }
        from.debit(nextTxnId(), amount, Transaction.Type.TRANSFER_OUT,
                "Transfer to " + to.getAccountNumber());
        to.credit(nextTxnId(), amount, Transaction.Type.TRANSFER_IN,
                "Transfer from " + from.getAccountNumber());
        save();
        return new TransferResult(from.getBalance(), to.getBalance());
    }

    public synchronized List<Transaction> getStatement(String accountNumber) throws AccountNotFoundException {
        return getAccount(accountNumber).getTransactions();
    }

    // ----------------------------------------------------------------
    // Persistence
    // ----------------------------------------------------------------

    private String nextTxnId() {
        return "TXN" + String.format("%06d", transactionSeq++);
    }

    private static String hash(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(text.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            // Fallback: should never happen with SHA-256/UTF-8, but avoid crashing the app.
            return String.valueOf(text.hashCode());
        }
    }

    /** Simple serializable wrapper bundling all persistent state together. */
    private static class Data implements Serializable {
        private static final long serialVersionUID = 1L;
        Map<String, User> users;
        Map<String, Account> accounts;
        int accountNumberSeq;
        int transactionSeq;
    }

    public synchronized void save() {
        try {
            File dir = new File(DATA_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            Data data = new Data();
            data.users = this.usersByUsername;
            data.accounts = this.accountsByNumber;
            data.accountNumberSeq = this.accountNumberSeq;
            data.transactionSeq = this.transactionSeq;
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
                out.writeObject(data);
            }
        } catch (IOException e) {
            System.err.println("Warning: could not save bank data - " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public synchronized void load() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return;
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            Data data = (Data) in.readObject();
            this.usersByUsername.clear();
            this.usersByUsername.putAll(data.users);
            this.accountsByNumber.clear();
            this.accountsByNumber.putAll(data.accounts);
            this.accountNumberSeq = data.accountNumberSeq;
            this.transactionSeq = data.transactionSeq;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Warning: could not load existing bank data - " + e.getMessage());
        }
    }
}
