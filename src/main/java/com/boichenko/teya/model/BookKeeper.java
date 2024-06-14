package com.boichenko.teya.model;

import com.boichenko.teya.model.exception.UserExistsException;
import com.boichenko.teya.model.exception.UserNotExistsException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class BookKeeper {

    private final Map<Integer, User> users;
    private final Map<User, Account> accounts;
    private final AtomicInteger usersCount;

    public BookKeeper() {
        this.users = new ConcurrentHashMap<>();
        this.accounts = new ConcurrentHashMap<>();
        this.usersCount = new AtomicInteger();
    }

    public int registerUser(String firstName, String lastName) {
        if (accounts.containsKey(new User(firstName, lastName))) {
            throw new UserExistsException();
        }

        int userId = this.usersCount.addAndGet(1);

        User user = new User(firstName, lastName);
        users.put(userId, user);
        accounts.put(user, new Account(user));

        return userId;
    }

    public Account account(int userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new UserNotExistsException();
        }

        var account = accounts.get(user);
        if (account == null) {
            throw new UserNotExistsException();
        }

        return account;
    }

}
