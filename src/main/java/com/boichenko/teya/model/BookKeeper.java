package com.boichenko.teya.model;

import com.boichenko.teya.model.exception.UserAlreadyRegisteredException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class BookKeeper {

    private final Map<User, Account> accounts;
    private final AtomicInteger usersCount;

    public BookKeeper() {
        this.accounts = new ConcurrentHashMap<>();
        this.usersCount = new AtomicInteger();
    }

    public int registerUser(String firstName, String lastName) {
        if (accounts.containsKey(new User(firstName, lastName))) {
            throw new UserAlreadyRegisteredException();
        }

        int userId = this.usersCount.addAndGet(1);
        User user = new User(firstName, lastName, userId);

        accounts.put(user, new Account(user));

        return userId;
    }

}
