# Portfolio Management Application (JavaFX)

## Authors
- Daniella Hordnes Dominey
- Bertalan Tóth
- Balázs Vincze

## Project Description
A JavaFX-based desktop application for managing financial portfolios, including **stocks and cryptocurrencies**.  
Users can track, trade, and analyze their investments, with support for multiple account types, event logging, and visualizations.  

Inspired by real-world platforms like TradingView, Bloomberg, and Coinbase, while remaining fully local and file-based for educational purposes.

---

## Key Features
- Portfolio creation and management
- Stock and cryptocurrency tracking
- Buy and sell transactions
- Cash transfers between accounts
- Multiple account types:
  - Checking and Savings Accounts
  - Broker Accounts
  - Crypto Wallets
- Event and alert logging
- Whale alert monitoring via CSV blockchain data
- Portfolio value and allocation visualizations
- Local data persistence (JSON and CSV)
- Optional portfolio encryption

---

## Technologies Used
- **Java 25**  
- **JavaFX 21.0.6** (FXML + CSS)  
- **Maven** for dependency management  
- **Jackson Databind** for JSON serialization  
- **Java Cryptography API (JCA)** for optional encryption  
- UI libraries: ControlsFX, FormsFX, BootstrapFX, TilesFX  

---

## Core Domain Model

### User
- Owns multiple portfolios
- Authentication handled locally via CSV-based login
- User data stored on the local machine

### Portfolio
- Central aggregation unit for assets, positions, transactions, and events
- Handles buy/sell logic and portfolio value calculation
- Supports CSV export and safe copying for simulations

### Assets
- Abstract `Asset` class with concrete types:
  - **Stock:** indivisible, identified by symbol, exchange, ISIN
  - **Crypto:** divisible, identified by symbol, blockchain, contract address
- Assets are uniquely identified by symbol to maintain consistent positions

### Accounts
- Abstract `Account` class: id, label, currency, balance, core operations
- Concrete types:
  - **CheckingAccount** – bank account for trading & transfers
  - **SavingsAccount** – limited withdrawals, resets annually
  - **BrokerAccount** – trades assets
  - **CryptoWallet** – blockchain transactions

### Transactions & Events
- Transactions: BUY / SELL / DEPOSIT / WITHDRAW / TRANSFER
- Event logging: notable actions, whale alerts
- Logic ensures correct portfolio updates and transaction validation

---

## Business Logic & Services
- **Money Operations:** centralized validation, money transfers, currency compatibility
- **Portfolio Logic:** ownership changes handled in `Portfolio.apply(Transaction)`  
- **Data Management:** JSON for portfolios/accounts, CSV for login & whale alerts, DTO reconstruction for consistency

---

## User Interface
- Built with FXML layouts and CSS styling
- Dark-themed, card-based design
- Separate views for:
  - Login and registration
  - Main dashboard
  - Trading & transfers
- Dashboard includes:
  - Portfolio value charts
  - Asset allocation pie charts
  - Transaction history
  - Events and whale alerts

---

## Optional Encryption (Advanced Feature)
- AES-GCM for authenticated encryption
- PBKDF2 for key derivation
- Encrypted files automatically detected and decrypted at load time

---

## Summary
A **well-structured JavaFX desktop application** demonstrating:
- Object-oriented design  
- Realistic financial logic  
- Modular UI and local persistence  
- Optional security features  

This project serves as an **educational example** for portfolio management applications and highlights key skills in software design, UI development, and finance-related programming.
