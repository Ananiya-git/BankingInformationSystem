# Banking Information System (Core Java Prototype)

A desktop prototype of a banking information system built with **Core Java
and Swing**. It demonstrates registration, login, account management,
deposits/withdrawals, fund transfers, account statements, and basic error
handling — all backed by simple file-based persistence so data survives
between runs.

## Requirements

- **JDK 8 or later** (needs `javac` and `java`; a JRE-only install is not
  enough since the project must be compiled from source).

## Project Structure

```
BankingInformationSystem/
├── src/
│   └── com/bank/
│       ├── Main.java                     # Application entry point
│       ├── model/
│       │   ├── User.java                 # Registered user + profile details
│       │   ├── Account.java              # A bank account and its balance
│       │   └── Transaction.java          # A single ledger entry
│       ├── exception/
│       │   ├── AuthenticationException.java
│       │   ├── AccountNotFoundException.java
│       │   ├── InsufficientFundsException.java
│       │   └── InvalidTransactionException.java
│       ├── service/
│       │   └── BankService.java          # All business logic + persistence
│       └── ui/
│           ├── LoginFrame.java
│           ├── RegisterFrame.java
│           ├── DashboardFrame.java
│           ├── DepositWithdrawDialog.java
│           ├── TransferDialog.java
│           ├── StatementDialog.java
│           └── AccountManagementDialog.java
├── data/                                 # Created automatically to persist data
├── compile.sh / compile.bat
├── run.sh / run.bat
└── README.md
```

## How to Compile & Run

**Mac / Linux**
```bash
cd BankingInformationSystem
chmod +x compile.sh run.sh
./compile.sh
./run.sh
```

**Windows**
```bat
cd BankingInformationSystem
compile.bat
run.bat
```

**Manually (any OS)**
```bash
cd BankingInformationSystem
javac -d bin $(find src -name "*.java")   # or list files explicitly on Windows
java -cp bin com.bank.Main
```

Always launch the app from the `BankingInformationSystem` folder — the
`data/bank_data.ser` file is created relative to your current directory.

## How Each Requirement Is Implemented

| # | Requirement | Where it lives |
|---|---|---|
| 1 | User Registration | `RegisterFrame` (form) + `BankService.registerUser()` — validates input, creates a `User` + first `Account`, generates a unique account number (`ACC100001`, `ACC100002`, ...), and shows a confirmation dialog with that number. |
| 2 | Account Management | `AccountManagementDialog` + `BankService.updateUserDetails()` / `changePassword()` — view/edit name, address, phone, email, and optionally reset the password. |
| 3 | Deposit & Withdrawal | `DepositWithdrawDialog` + `BankService.deposit()` / `withdraw()` — updates the balance and shows a confirmation with the transaction amount and resulting balance. |
| 4 | Fund Transfer | `TransferDialog` + `BankService.transfer()` — moves funds between any two account numbers (own or another user's) and reports both updated balances. |
| 5 | Account Statements | `StatementDialog` + `BankService.getStatement()` — tabular history of every transaction: date/time, type, amount, and balance after. |
| 6 | Password Protection | Passwords are SHA-256 hashed (`BankService.hash()`) before storage; `LoginFrame` + `BankService.login()` authenticate before granting dashboard access. |
| 7 | Error Handling | Custom checked exceptions (`InsufficientFundsException`, `InvalidTransactionException`, `AccountNotFoundException`, `AuthenticationException`) are thrown by the service layer and caught in the UI, surfaced via `JOptionPane` error dialogs (e.g. overdrawing an account, duplicate usernames, non-numeric amounts). |
| 8 | User Interface | Full Swing GUI: `LoginFrame` → `RegisterFrame` / `DashboardFrame` → feature dialogs, using tables, forms, and dialogs for a friendly click-through experience. |
| 9 | Persistence | `BankService.save()` / `load()` serialize all users, accounts, and transactions to `data/bank_data.ser` after every change, and reload automatically on startup — so the "session" can be closed and reopened without losing data. |

## Using the App

1. Run the app — the **Login** screen appears.
2. Click **Create New Account** to register (name, address, phone, email,
   username, password, initial deposit). Note the account number shown.
3. Log in with your username/password.
4. From the **Dashboard** you can:
   - Select a row and use **Deposit / Withdraw**, **Transfer Funds**, or
     **View Statement**.
   - **Open New Account** to add a second account under the same profile.
   - **Manage Profile** to update your details or password.
   - **Refresh** to reload balances, **Logout** to return to the login screen.
5. To try a **transfer to another user**, register a second account (log out,
   register again with a different username), then transfer using that
   user's account number.

## Notes on Design Choices

- **Persistence** uses Java object serialization to a local `data/`
  folder rather than a database, keeping the prototype dependency-free
  while still meeting the "store data between operations" requirement.
- **Account numbers** are generated sequentially (`ACC100001`, `ACC100002`, ...)
  to guarantee uniqueness.
- **Passwords** are never stored in plain text — only their SHA-256 hash is
  persisted.
- The `BankService` singleton is the single source of truth in memory; all
  UI screens read/write through it, which keeps the Swing code focused on
  presentation only.

## Possible Extensions (Beyond Prototype Scope)

- Multi-user concurrent access (currently single-process desktop app).
- A real relational database (JDBC) instead of file serialization.
- Interest calculation, scheduled payments, or account types (savings/checking).
- Admin screens for bank staff to view all users/accounts.
