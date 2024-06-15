package com.boichenko.teya.model;

import com.boichenko.teya.model.exception.NegativeOrZeroTransactionAmountException;
import com.boichenko.teya.model.exception.UserNotActiveException;
import com.boichenko.teya.model.exception.UserNotFoundException;
import com.boichenko.teya.model.transaction.P2P;
import com.boichenko.teya.model.transaction.Transaction;
import com.boichenko.teya.model.transaction.UserTransaction;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class BookKeeper {

    private final UsersKeeper usersKeeper;
    private final Map<UserID, Account> accounts;

    public BookKeeper() {
        this.usersKeeper = new UsersKeeper();
        this.accounts = new HashMap<>();
    }

    public UserID registerUser(String firstName, String lastName) {
        UserID userID = usersKeeper.registerUser(firstName, lastName);
        accounts.put(userID, new Account(userID));
        return userID;
    }

    public void activateUser(UserID userID) {
        usersKeeper.activateUser(userID);
    }

    public void deactivateUser(UserID userID) {
        usersKeeper.deactivateUser(userID);
    }

    public Account account(UserID userID) {
        var account = accounts.get(userID);
        if (account == null) {
            throw new UserNotFoundException();
        }

        return account;
    }

    public void saveTransaction(Transaction t) {
        if (BigDecimal.ZERO.compareTo(t.amount()) >= 0) {
            throw new NegativeOrZeroTransactionAmountException();
        }

        switch (t.type()) {
            case IN, OUT -> {
                UserTransaction userTransaction = (UserTransaction) t;
                Account account = activeUserAccount(userTransaction.userId());
                account.addTransaction(t);
            }
            case P2P -> {
                P2P p2p = (P2P) t;
                Account from = activeUserAccount(p2p.from());
                Account to = activeUserAccount(p2p.to());

                from.addTransaction(t);
                to.addTransaction(t);
            }
        }
    }

    private Account activeUserAccount(UserID userID) {
        if (!usersKeeper.isUserActive(userID)) {
            throw new UserNotActiveException();
        }

        return account(userID);
    }

}
