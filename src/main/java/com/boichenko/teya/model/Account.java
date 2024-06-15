package com.boichenko.teya.model;

import com.boichenko.teya.model.transaction.P2P;
import com.boichenko.teya.model.transaction.Transaction;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

public class Account {
    private final UserID user;
    private final List<Transaction> transactions;
    private BigDecimal balance;

    public Account(UserID user) {
        this.user = user;
        this.transactions = new LinkedList<>();
        this.balance = BigDecimal.ZERO;
    }

    public void addTransaction(Transaction t) {
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
    }


    public List<Transaction> transactions() {
        return transactions;
    }

    public BigDecimal balance() {
        return balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        if (!user.equals(account.user)) return false;
        if (!transactions.equals(account.transactions)) return false;
        return balance.equals(account.balance);
    }

    @Override
    public int hashCode() {
        int result = user.hashCode();
        result = 31 * result + transactions.hashCode();
        result = 31 * result + balance.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Account{" +
                "user=" + user +
                ", transactions=" + transactions +
                ", balance=" + balance +
                '}';
    }
}
