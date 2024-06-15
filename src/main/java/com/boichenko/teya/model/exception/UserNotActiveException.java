package com.boichenko.teya.model.exception;

import com.boichenko.teya.model.UserID;

public class UserNotActiveException extends RuntimeException {

    private final UserID userID;

    public UserNotActiveException(UserID userID) {
        this.userID = userID;
    }

    public UserID userID() {
        return userID;
    }
}
