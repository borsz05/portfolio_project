package org.isep.portfolioproject.model;

import java.util.ArrayList;
import java.util.List;

public class User {
private String id;
private String username;
private String password;
private List<Portfolio> portfolios = new ArrayList<>();


public User(String id, String username, String password) {
    this.id = id;
    this.username = username;
    this.password = password;
}

public void addPortfolio(Portfolio portfolio) {
    if (portfolio == null) {
        throw new IllegalArgumentException("Portfolio can't be null");
    }

    portfolios.add(portfolio);
}

public List<Portfolio> getPortfolios() {
    return new ArrayList<>(portfolios);
}

public String getId() {
    return id;
}

public String getUsername() {
    return username;
}

public String getPassword() {
    return password;
}

}
