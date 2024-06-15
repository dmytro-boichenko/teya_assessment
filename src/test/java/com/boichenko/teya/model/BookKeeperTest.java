package com.boichenko.teya.model;

import com.boichenko.teya.model.exception.NegativeOrZeroTransactionAmountException;
import com.boichenko.teya.model.exception.NotEnoughMoneyException;
import com.boichenko.teya.model.exception.UserExistsException;
import com.boichenko.teya.model.exception.UserNotExistsException;
import com.boichenko.teya.model.transaction.In;
import com.boichenko.teya.model.transaction.Out;
import com.boichenko.teya.model.transaction.P2P;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookKeeperTest {

    @Test
    void success_AddTransaction_EmptyUser() {
        BookKeeper bookKeeper = new BookKeeper();

        UserID userId = bookKeeper.registerUser("john", "doe");
        assertEquals(new UserID(1), userId);

        Account account = bookKeeper.account(userId);
        assertEquals(new Account(userId), account);
    }

    @Test
    void failure_AddTransaction_SameUser() {
        BookKeeper bookKeeper = new BookKeeper();

        UserID userId = bookKeeper.registerUser("john", "doe");
        assertEquals(new UserID(1), userId);

        assertThrows(UserExistsException.class, () -> bookKeeper.registerUser("john", "doe"));
    }

    @Test
    void failure_UserNotFound() {
        BookKeeper bookKeeper = new BookKeeper();

        UserID userId = bookKeeper.registerUser("john", "doe");
        assertEquals(new UserID(1), userId);

        assertThrows(UserNotExistsException.class, () -> bookKeeper.account(new UserID(2)));
    }

    @Test
    void success_AddTransaction_Single() {
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
    void failure_AddTransaction_Single_NegativeTopUp() {
        BookKeeper bookKeeper = new BookKeeper();

        UserID userId = bookKeeper.registerUser("john", "doe");
        assertEquals(new UserID(1), userId);

        Account account = bookKeeper.account(userId);
        assertEquals(new Account(userId), account);

        BigDecimal amount = new BigDecimal("-12.34");
        assertThrows(NegativeOrZeroTransactionAmountException.class,
                () -> bookKeeper.saveTransaction(new In(userId, amount))
        );
        assertEquals(0, BigDecimal.ZERO.compareTo(account.balance()));
        assertIterableEquals(List.of(), account.transactions());
    }

    @Test
    void success_AddTransaction_Two() {
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

    @Test
    void success_AddTransaction_Two_Zero() {
        BookKeeper bookKeeper = new BookKeeper();

        UserID userId = bookKeeper.registerUser("john", "doe");
        assertEquals(new UserID(1), userId);

        Account account = bookKeeper.account(userId);
        assertEquals(new Account(userId), account);

        BigDecimal amount = new BigDecimal("12.34");
        bookKeeper.saveTransaction(new In(userId, amount));
        assertEquals(amount, account.balance());
        assertIterableEquals(
                List.of(new In(userId, amount)),
                account.transactions()
        );

        bookKeeper.saveTransaction(new Out(userId, amount));
        assertEquals(0, BigDecimal.ZERO.compareTo(account.balance()));
        assertIterableEquals(
                List.of(new In(userId, amount), new Out(userId, amount)),
                account.transactions()
        );
    }

    @Test
    void failure_AddTransaction_Two_NotEnoughBalance() {
        BookKeeper bookKeeper = new BookKeeper();

        UserID userId = bookKeeper.registerUser("john", "doe");
        assertEquals(new UserID(1), userId);

        Account account = bookKeeper.account(userId);
        assertEquals(new Account(userId), account);

        BigDecimal amount1 = new BigDecimal("12.34");
        bookKeeper.saveTransaction(new In(userId, amount1));
        assertEquals(amount1, account.balance());
        assertIterableEquals(
                List.of(new In(userId, amount1)),
                account.transactions()
        );

        BigDecimal amount2 = new BigDecimal("23.45");
        assertThrows(NotEnoughMoneyException.class,
                () -> bookKeeper.saveTransaction(new Out(userId, amount2))
        );
    }

    @Test
    void success_AddTransaction_P2P() {
        BookKeeper bookKeeper = new BookKeeper();

        UserID userId1 = bookKeeper.registerUser("john", "doe");
        assertEquals(new UserID(1), userId1);
        Account account1 = bookKeeper.account(userId1);
        assertEquals(new Account(userId1), account1);

        UserID userId2 = bookKeeper.registerUser("jane", "doe");
        assertEquals(new UserID(2), userId2);
        Account account2 = bookKeeper.account(userId2);
        assertEquals(new Account(userId2), account2);


        BigDecimal amount1 = new BigDecimal("23.45");
        bookKeeper.saveTransaction(new In(userId1, amount1));
        assertEquals(amount1, account1.balance());
        assertIterableEquals(
                List.of(new In(userId1, amount1)),
                account1.transactions()
        );

        BigDecimal amount2 = new BigDecimal("12.34");
        bookKeeper.saveTransaction(new P2P(userId1, userId2, amount2));
        assertEquals(amount1.subtract(amount2), account1.balance());
        assertIterableEquals(
                List.of(new In(userId1, amount1), new P2P(userId1, userId2, amount2)),
                account1.transactions()
        );

        assertEquals(amount2, account2.balance());
        assertIterableEquals(
                List.of(new P2P(userId1, userId2, amount2)),
                account2.transactions()
        );
    }

    @Test
    void failure_AddTransaction_P2P_NotEnoughMoney() {
        BookKeeper bookKeeper = new BookKeeper();

        UserID userId1 = bookKeeper.registerUser("john", "doe");
        assertEquals(new UserID(1), userId1);
        Account account1 = bookKeeper.account(userId1);
        assertEquals(new Account(userId1), account1);

        UserID userId2 = bookKeeper.registerUser("jane", "doe");
        assertEquals(new UserID(2), userId2);
        Account account2 = bookKeeper.account(userId2);
        assertEquals(new Account(userId2), account2);


        BigDecimal amount1 = new BigDecimal("12.34");
        bookKeeper.saveTransaction(new In(userId1, amount1));
        assertEquals(amount1, account1.balance());
        assertIterableEquals(
                List.of(new In(userId1, amount1)),
                account1.transactions()
        );

        BigDecimal amount2 = new BigDecimal("23.45");

        assertThrows(NotEnoughMoneyException.class,
                () -> bookKeeper.saveTransaction(new P2P(userId1, userId2, amount2))
        );

        assertEquals(amount1, account1.balance());
        assertIterableEquals(
                List.of(new In(userId1, amount1)),
                account1.transactions()
        );

        assertEquals(0, BigDecimal.ZERO.compareTo(account2.balance()));
        assertIterableEquals(
                List.of(),
                account2.transactions()
        );
    }
}