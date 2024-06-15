package com.boichenko.teya.model;

import com.boichenko.teya.model.exception.UserExistsException;
import com.boichenko.teya.model.exception.UserNotExistsException;
import com.boichenko.teya.model.transaction.P2P;
import com.boichenko.teya.model.transaction.Transaction;
import com.boichenko.teya.model.transaction.UserTransaction;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class BookKeeper {

    private final Map<User, UserID> users;
    private final Map<UserID, Account> accounts;
    private final AtomicInteger usersCount;

    public BookKeeper() {
        this.users = new ConcurrentHashMap<>();
        this.accounts = new ConcurrentHashMap<>();
        this.usersCount = new AtomicInteger();
    }

    public UserID registerUser(String firstName, String lastName) {
        if (users.containsKey(new User(firstName, lastName))) {
            throw new UserExistsException();
        }

        UserID userId = new UserID(this.usersCount.addAndGet(1));
        users.put(new User(firstName, lastName), userId);
        accounts.put(userId, new Account(userId));

        return userId;
    }

    public Account account(UserID userId) {
        var account = accounts.get(userId);
        if (account == null) {
            throw new UserNotExistsException();
        }

        return account;
    }

    public void saveTransaction(Transaction t) {
        switch (t.type()) {
            case IN, OUT -> {
                UserTransaction userTransaction = (UserTransaction) t;
                Account account = account(userTransaction.userId());
                account.addTransaction(t);
            }
            case P2P -> {
                P2P p2p = (P2P) t;
                Account from = account(p2p.from());
                from.addTransaction(t);

                Account to = account(p2p.to());
                to.addTransaction(t);
            }
        }
    }
}
