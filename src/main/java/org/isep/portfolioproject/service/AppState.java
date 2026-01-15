package org.isep.portfolioproject.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.isep.portfolioproject.model.Portfolio;
import org.isep.portfolioproject.model.accounts.BrokerAccount;
import org.isep.portfolioproject.model.accounts.CheckingAccount;
import org.isep.portfolioproject.model.accounts.SavingAccount;
import org.isep.portfolioproject.util.Currency;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

public class AppState {

    private static AppState instance;

    private final DataManager dataManager;
    private final PriceProvider priceProvider;
    private final AnalysisService analysisService;
    private final BlockchainMonitor blockchainMonitor;

    private final CheckingAccount checkingAccount;
    private final SavingAccount savingAccount;
    private final BrokerAccount brokerAccount;

    private List<Portfolio> portfolios;
    private Portfolio selectedPortfolio;
    private final String username;
    private final String accountsPath;

    private AppState(DataManager dataManager, PriceProvider priceProvider, String passphrase, String username, String accountsPath) {
        this.dataManager = dataManager;
        this.priceProvider = priceProvider;
        this.analysisService = new AnalysisService();
        this.blockchainMonitor = new BlockchainMonitor(150000.0);

        this.passphrase = passphrase;
        this.portfolios = dataManager.loadPortfolios(passphrase);
        this.selectedPortfolio = portfolios.isEmpty() ? null : portfolios.get(0);

        this.username = username;
        this.accountsPath = accountsPath;
        AccountSnapshot snapshot = loadAccounts(accountsPath);

        this.checkingAccount = new CheckingAccount(
                snapshot.checkingIban,
                "LocalBank",
                "CHK-" + UUID.randomUUID(),
                "Checking",
                Currency.USD,
                snapshot.checkingBalance
        );
        this.savingAccount = new SavingAccount(
                "SAV-" + UUID.randomUUID(),
                "Saving",
                Currency.USD,
                snapshot.savingBalance
        );
        this.brokerAccount = new BrokerAccount(
                "DemoBroker",
                "BR-7788",
                "BRK-" + UUID.randomUUID(),
                "Broker",
                Currency.USD,
                snapshot.brokerBalance
        );
    }

    public static void init(DataManager dataManager, PriceProvider priceProvider, String passphrase, String username, String accountsPath) {
        instance = new AppState(dataManager, priceProvider, passphrase, username, accountsPath);
    }

    public static AppState get() {
        if (instance == null) throw new IllegalStateException("AppState not initialized");
        return instance;
    }

    public void save() {
        dataManager.setPassphrase(getPassphrase());
        dataManager.savePortfolios(portfolios);
        saveAccounts(accountsPath);
    }

    private String passphrase;

    public String getPassphrase() {
        return passphrase;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    public List<Portfolio> getPortfolios() {
        return portfolios;
    }

    public void setPortfolios(List<Portfolio> portfolios) {
        this.portfolios = portfolios;
    }

    public Portfolio getSelectedPortfolio() {
        return selectedPortfolio;
    }

    public void setSelectedPortfolio(Portfolio selectedPortfolio) {
        this.selectedPortfolio = selectedPortfolio;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public PriceProvider getPriceProvider() {
        return priceProvider;
    }

    public AnalysisService getAnalysisService() {
        return analysisService;
    }

    public BlockchainMonitor getBlockchainMonitor() {
        return blockchainMonitor;
    }

    public CheckingAccount getCheckingAccount() {
        return checkingAccount;
    }

    public SavingAccount getSavingAccount() {
        return savingAccount;
    }

    public BrokerAccount getBrokerAccount() {
        return brokerAccount;
    }

    public String getUsername() {
        return username;
    }

    public static void clear() {
        instance = null;
    }

    private void saveAccounts(String path) {
        try {
            AccountSnapshot snapshot = new AccountSnapshot();
            snapshot.checkingBalance = checkingAccount.getBalance();
            snapshot.savingBalance = savingAccount.getBalance();
            snapshot.brokerBalance = brokerAccount.getBalance();
            snapshot.checkingIban = checkingAccount.getIban();

            File file = new File(path);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, snapshot);
        } catch (Exception e) {
            throw new RuntimeException("Could not save accounts", e);
        }
    }

    private AccountSnapshot loadAccounts(String path) {
        File file = new File(path);
        if (!file.exists()) {
            AccountSnapshot snapshot = new AccountSnapshot();
            snapshot.checkingBalance = 2500.0;
            snapshot.savingBalance = 5000.0;
            snapshot.brokerBalance = 3000.0;
            snapshot.checkingIban = "NO" + UUID.randomUUID().toString().substring(0, 6);
            return snapshot;
        }
        try {
            byte[] raw = Files.readAllBytes(file.toPath());
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(raw, AccountSnapshot.class);
        } catch (Exception e) {
            AccountSnapshot snapshot = new AccountSnapshot();
            snapshot.checkingBalance = 2500.0;
            snapshot.savingBalance = 5000.0;
            snapshot.brokerBalance = 3000.0;
            snapshot.checkingIban = "NO" + UUID.randomUUID().toString().substring(0, 6);
            return snapshot;
        }
    }

    public static class AccountSnapshot {
        public double checkingBalance;
        public double savingBalance;
        public double brokerBalance;
        public String checkingIban;
    }
}
