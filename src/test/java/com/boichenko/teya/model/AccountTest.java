package com.boichenko.teya.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void addTransaction__EmptyUser() {
        BookKeeper bookKeeper = new BookKeeper();

        int userId = bookKeeper.registerUser("john", "doe");
        assertEquals(1, userId);

        Account account = bookKeeper.account(userId);
        assertEquals(new Account(new User("john", "doe")), account);
    }
}