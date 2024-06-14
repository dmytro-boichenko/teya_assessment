package com.boichenko.teya.model;

import com.boichenko.teya.model.transaction.P2P;
import com.boichenko.teya.model.transaction.Transaction;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

public class Account {
    private final User user;
    private final List<Transaction> transactions;
    private BigDecimal balance;

    public Account(User user) {
        this.user = user;
        this.transactions = new LinkedList<>();
        this.balance = BigDecimal.ZERO;
    }

    public BigDecimal addTransaction(Transaction t) {
        transactions.add(t);

        switch (t.type()) {
            case IN -> this.balance = this.balance.add(t.amount());
            case OUT -> this.balance = this.balance.subtract(t.amount());
            case P2P -> {
                P2P p2p = (P2P) t;

                if (this.user.equals(p2p.from())) {
                    this.balance = this.balance.subtract(t.amount());
                } else {
                    this.balance = this.balance.add(t.amount());
                }
            }
        }

        return this.balance;
    }

    public User user() {
        return user;
    }

    public List<Transaction> transactions() {
        return transactions;
    }

    public BigDecimal balance() {
        return balance;
    }
}
