package com.boichenko.teya.model.exception;

import com.boichenko.teya.model.UserID;

public class UserActiveException extends RuntimeException {
    private final UserID userID;

    public UserActiveException(UserID userID) {
        this.userID = userID;
    }

    public UserID userID() {
        return userID;
    }
}
