package com.boichenko.teya.model;

import com.boichenko.teya.model.exception.NotEnoughMoneyException;
import com.boichenko.teya.model.transaction.P2P;
import com.boichenko.teya.model.transaction.Transaction;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

public class Account {
    private final UserID userID;
    private final List<Transaction> transactions;
    private BigDecimal balance;

    public Account(UserID userID) {
        this.userID = userID;
        this.transactions = new LinkedList<>();
        this.balance = BigDecimal.ZERO;
    }

    public void addTransaction(Transaction t) {
        switch (t.type()) {
            case IN -> addBalance(this.userID, t.amount());
            case OUT -> subtractBalance(this.userID, t.amount());
            case P2P -> {
                P2P p2p = (P2P) t;

                if (this.userID.equals(p2p.from())) {
                    subtractBalance(p2p.from(), t.amount());
                } else {
                    addBalance(p2p.to(), t.amount());
                }
            }
        }

        transactions.add(t);
    }


    public List<Transaction> transactions() {
        return transactions;
    }

    public BigDecimal balance() {
        return balance;
    }

    public UserID userID() {
        return userID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        if (!userID.equals(account.userID)) return false;
        if (!transactions.equals(account.transactions)) return false;
        return balance.equals(account.balance);
    }

    @Override
    public int hashCode() {
        int result = userID.hashCode();
        result = 31 * result + transactions.hashCode();
        result = 31 * result + balance.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Account{" +
                "user=" + userID +
                ", transactions=" + transactions +
                ", balance=" + balance +
                '}';
    }

    private void addBalance(UserID userID, BigDecimal amount) {
        BigDecimal b = this.balance.add(amount);
        if (BigDecimal.ZERO.compareTo(b) > 0) {
            throw new NotEnoughMoneyException(userID);
        }
        this.balance = b;
    }

    private void subtractBalance(UserID userID, BigDecimal amount) {
        BigDecimal b = this.balance.subtract(amount);
        if (BigDecimal.ZERO.compareTo(b) > 0) {
            throw new NotEnoughMoneyException(userID);
        }
        this.balance = b;
    }
}
