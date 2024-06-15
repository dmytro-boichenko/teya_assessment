package com.boichenko.teya.model.exception;

import com.boichenko.teya.model.UserID;

public class NotEnoughMoneyException extends RuntimeException {

    private final UserID userID;

    public NotEnoughMoneyException(UserID userID) {
        this.userID = userID;
    }

    public UserID userID() {
        return userID;
    }
}
