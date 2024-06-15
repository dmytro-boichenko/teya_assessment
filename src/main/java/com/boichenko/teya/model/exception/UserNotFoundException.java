package com.boichenko.teya.model.exception;

import com.boichenko.teya.model.UserID;

public class UserNotFoundException extends RuntimeException {

    private final UserID userID;

    public UserNotFoundException(UserID userID) {
        this.userID = userID;
    }

    public UserID userID() {
        return userID;
    }
}
