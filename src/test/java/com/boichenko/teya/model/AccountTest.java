package com.boichenko.teya.model;

import com.boichenko.teya.model.transaction.In;
import com.boichenko.teya.model.transaction.Out;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void addTransaction_EmptyUser() {
        BookKeeper bookKeeper = new BookKeeper();

        UserID userId = bookKeeper.registerUser("john", "doe");
        assertEquals(new UserID(1), userId);

        Account account = bookKeeper.account(userId);
        assertEquals(new Account(userId), account);
    }

    @Test
    void addTransaction_Single() {
        BookKeeper bookKeeper = new BookKeeper();

        UserID userId = bookKeeper.registerUser("john", "doe");
        assertEquals(new UserID(1), userId);

        Account account = bookKeeper.account(userId);
        assertEquals(new Account(userId), account);

        BigDecimal amount = new BigDecimal("12.34");
        bookKeeper.saveTransaction(new In(userId, amount));
        assertEquals(amount, account.balance());
        assertIterableEquals(List.of(new In(userId, amount)), account.transactions());
    }

    @Test
    void addTransaction_Two() {
        BookKeeper bookKeeper = new BookKeeper();

        UserID userId = bookKeeper.registerUser("john", "doe");
        assertEquals(new UserID(1), userId);

        Account account = bookKeeper.account(userId);
        assertEquals(new Account(userId), account);

        BigDecimal amount1 = new BigDecimal("23.45");
        bookKeeper.saveTransaction(new In(userId, amount1));
        assertEquals(amount1, account.balance());
        assertIterableEquals(
                List.of(new In(userId, amount1)),
                account.transactions()
        );

        BigDecimal amount2 = new BigDecimal("12.34");
        bookKeeper.saveTransaction(new Out(userId, amount2));
        assertEquals(amount1.subtract(amount2), account.balance());
        assertIterableEquals(
                List.of(new In(userId, amount1), new Out(userId, amount2)),
                account.transactions()
        );
    }
}