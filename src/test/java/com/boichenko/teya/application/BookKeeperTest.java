package com.boichenko.teya.application;

import com.boichenko.teya.model.Account;
import com.boichenko.teya.model.UserID;
import com.boichenko.teya.model.exception.*;
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

        UserID userID = bookKeeper.registerUser("john", "doe");
        assertEquals(new UserID(1), userID);

        Account account = bookKeeper.account(userID);
        assertEquals(new Account(userID), account);
    }

    @Test
    void failure_AddTransaction_SameUser() {
        BookKeeper bookKeeper = new BookKeeper();

        UserID userID = bookKeeper.registerUser("john", "doe");
        assertEquals(new UserID(1), userID);

        assertThrows(UserAlreadyRegisteredException.class, () -> bookKeeper.registerUser("john", "doe"));
    }

    @Test
    void failure_UserNotFound() {
        BookKeeper bookKeeper = new BookKeeper();

        UserID userID = bookKeeper.registerUser("john", "doe");
        assertEquals(new UserID(1), userID);

        assertThrows(UserNotFoundException.class, () -> bookKeeper.account(new UserID(2)));
    }

    @Test
    void success_AddTransaction_Single() {
        BookKeeper bookKeeper = new BookKeeper();

        UserID userID = bookKeeper.registerUser("john", "doe");
        assertEquals(new UserID(1), userID);

        Account account = bookKeeper.account(userID);
        assertEquals(new Account(userID), account);

        BigDecimal amount = new BigDecimal("12.34");
        bookKeeper.saveTransaction(new In(userID, amount));
        assertEquals(amount, account.balance());
        assertIterableEquals(List.of(new In(userID, amount)), account.transactions());
    }

    @Test
    void failure_AddTransaction_UserNotActive() {
        BookKeeper bookKeeper = new BookKeeper();

        UserID userID = bookKeeper.registerUser("john", "doe");
        assertEquals(new UserID(1), userID);

        Account account = bookKeeper.account(userID);
        assertEquals(new Account(userID), account);

        bookKeeper.deactivateUser(userID);

        BigDecimal amount = new BigDecimal("12.34");
        assertThrows(UserNotActiveException.class,
                () -> bookKeeper.saveTransaction(new In(userID, amount))
        );
        assertEquals(0, BigDecimal.ZERO.compareTo(account.balance()));
        assertIterableEquals(List.of(), account.transactions());
    }

    @Test
    void success_AddTransaction_UserReactivated() {
        BookKeeper bookKeeper = new BookKeeper();

        UserID userID = bookKeeper.registerUser("john", "doe");
        assertEquals(new UserID(1), userID);

        Account account = bookKeeper.account(userID);
        assertEquals(new Account(userID), account);

        bookKeeper.deactivateUser(userID);

        BigDecimal amount = new BigDecimal("12.34");
        assertThrows(UserNotActiveException.class,
                () -> bookKeeper.saveTransaction(new In(userID, amount))
        );
        assertEquals(0, BigDecimal.ZERO.compareTo(account.balance()));
        assertIterableEquals(List.of(), account.transactions());

        bookKeeper.activateUser(userID);

        bookKeeper.saveTransaction(new In(userID, amount));
        assertEquals(amount, account.balance());
        assertIterableEquals(List.of(new In(userID, amount)), account.transactions());
    }

    @Test
    void failure_AddTransaction_Single_NegativeTopUp() {
        BookKeeper bookKeeper = new BookKeeper();

        UserID userID = bookKeeper.registerUser("john", "doe");
        assertEquals(new UserID(1), userID);

        Account account = bookKeeper.account(userID);
        assertEquals(new Account(userID), account);

        BigDecimal amount = new BigDecimal("-12.34");
        assertThrows(NegativeOrZeroTransactionAmountException.class,
                () -> bookKeeper.saveTransaction(new In(userID, amount))
        );
        assertEquals(0, BigDecimal.ZERO.compareTo(account.balance()));
        assertIterableEquals(List.of(), account.transactions());
    }

    @Test
    void success_AddTransaction_Two() {
        BookKeeper bookKeeper = new BookKeeper();

        UserID userID = bookKeeper.registerUser("john", "doe");
        assertEquals(new UserID(1), userID);

        Account account = bookKeeper.account(userID);
        assertEquals(new Account(userID), account);

        BigDecimal amount1 = new BigDecimal("23.45");
        bookKeeper.saveTransaction(new In(userID, amount1));
        assertEquals(amount1, account.balance());
        assertIterableEquals(
                List.of(new In(userID, amount1)),
                account.transactions()
        );

        BigDecimal amount2 = new BigDecimal("12.34");
        bookKeeper.saveTransaction(new Out(userID, amount2));
        assertEquals(amount1.subtract(amount2), account.balance());
        assertIterableEquals(
                List.of(new In(userID, amount1), new Out(userID, amount2)),
                account.transactions()
        );
    }

    @Test
    void success_AddTransaction_Two_Zero() {
        BookKeeper bookKeeper = new BookKeeper();

        UserID userID = bookKeeper.registerUser("john", "doe");
        assertEquals(new UserID(1), userID);

        Account account = bookKeeper.account(userID);
        assertEquals(new Account(userID), account);

        BigDecimal amount = new BigDecimal("12.34");
        bookKeeper.saveTransaction(new In(userID, amount));
        assertEquals(amount, account.balance());
        assertIterableEquals(
                List.of(new In(userID, amount)),
                account.transactions()
        );

        bookKeeper.saveTransaction(new Out(userID, amount));
        assertEquals(0, BigDecimal.ZERO.compareTo(account.balance()));
        assertIterableEquals(
                List.of(new In(userID, amount), new Out(userID, amount)),
                account.transactions()
        );
    }

    @Test
    void failure_AddTransaction_Two_NotEnoughBalance() {
        BookKeeper bookKeeper = new BookKeeper();

        UserID userID = bookKeeper.registerUser("john", "doe");
        assertEquals(new UserID(1), userID);

        Account account = bookKeeper.account(userID);
        assertEquals(new Account(userID), account);

        BigDecimal amount1 = new BigDecimal("12.34");
        bookKeeper.saveTransaction(new In(userID, amount1));
        assertEquals(amount1, account.balance());
        assertIterableEquals(
                List.of(new In(userID, amount1)),
                account.transactions()
        );

        BigDecimal amount2 = new BigDecimal("23.45");
        assertThrows(NotEnoughMoneyException.class,
                () -> bookKeeper.saveTransaction(new Out(userID, amount2))
        );
    }

    @Test
    void success_AddTransaction_P2P() {
        BookKeeper bookKeeper = new BookKeeper();

        UserID userID1 = bookKeeper.registerUser("john", "doe");
        assertEquals(new UserID(1), userID1);
        Account account1 = bookKeeper.account(userID1);
        assertEquals(new Account(userID1), account1);

        UserID userID2 = bookKeeper.registerUser("jane", "doe");
        assertEquals(new UserID(2), userID2);
        Account account2 = bookKeeper.account(userID2);
        assertEquals(new Account(userID2), account2);


        BigDecimal amount1 = new BigDecimal("23.45");
        bookKeeper.saveTransaction(new In(userID1, amount1));
        assertEquals(amount1, account1.balance());
        assertIterableEquals(
                List.of(new In(userID1, amount1)),
                account1.transactions()
        );

        BigDecimal amount2 = new BigDecimal("12.34");
        bookKeeper.saveTransaction(new P2P(userID1, userID2, amount2));
        assertEquals(amount1.subtract(amount2), account1.balance());
        assertIterableEquals(
                List.of(new In(userID1, amount1), new P2P(userID1, userID2, amount2)),
                account1.transactions()
        );

        assertEquals(amount2, account2.balance());
        assertIterableEquals(
                List.of(new P2P(userID1, userID2, amount2)),
                account2.transactions()
        );
    }

    @Test
    void failure_AddTransaction_P2P_NotEnoughMoney() {
        BookKeeper bookKeeper = new BookKeeper();

        UserID userID1 = bookKeeper.registerUser("john", "doe");
        assertEquals(new UserID(1), userID1);
        Account account1 = bookKeeper.account(userID1);
        assertEquals(new Account(userID1), account1);

        UserID userID2 = bookKeeper.registerUser("jane", "doe");
        assertEquals(new UserID(2), userID2);
        Account account2 = bookKeeper.account(userID2);
        assertEquals(new Account(userID2), account2);


        BigDecimal amount1 = new BigDecimal("12.34");
        bookKeeper.saveTransaction(new In(userID1, amount1));
        assertEquals(amount1, account1.balance());
        assertIterableEquals(
                List.of(new In(userID1, amount1)),
                account1.transactions()
        );

        BigDecimal amount2 = new BigDecimal("23.45");

        assertThrows(NotEnoughMoneyException.class,
                () -> bookKeeper.saveTransaction(new P2P(userID1, userID2, amount2))
        );

        assertEquals(amount1, account1.balance());
        assertIterableEquals(
                List.of(new In(userID1, amount1)),
                account1.transactions()
        );

        assertEquals(0, BigDecimal.ZERO.compareTo(account2.balance()));
        assertIterableEquals(
                List.of(),
                account2.transactions()
        );
    }

    @Test
    void failure_AddTransaction_P2P_OneUserNotActive() {
        BookKeeper bookKeeper = new BookKeeper();

        UserID userID1 = bookKeeper.registerUser("john", "doe");
        assertEquals(new UserID(1), userID1);
        Account account1 = bookKeeper.account(userID1);
        assertEquals(new Account(userID1), account1);

        UserID userID2 = bookKeeper.registerUser("jane", "doe");
        assertEquals(new UserID(2), userID2);
        Account account2 = bookKeeper.account(userID2);
        assertEquals(new Account(userID2), account2);


        BigDecimal amount1 = new BigDecimal("23.45");
        bookKeeper.saveTransaction(new In(userID1, amount1));
        assertEquals(amount1, account1.balance());
        assertIterableEquals(
                List.of(new In(userID1, amount1)),
                account1.transactions()
        );

        bookKeeper.deactivateUser(userID2);

        BigDecimal amount2 = new BigDecimal("12.34");
        assertThrows(UserNotActiveException.class,
                () -> bookKeeper.saveTransaction(new P2P(userID1, userID2, amount2))
        );
        assertEquals(amount1, account1.balance());
        assertIterableEquals(
                List.of(new In(userID1, amount1)),
                account1.transactions()
        );

        assertEquals(0, BigDecimal.ZERO.compareTo(account2.balance()));
        assertIterableEquals(
                List.of(),
                account2.transactions()
        );
    }
}

