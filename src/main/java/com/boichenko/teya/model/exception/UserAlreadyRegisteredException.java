package com.boichenko.teya.model.exception;

public class UserAlreadyRegisteredException extends RuntimeException {

    public UserAlreadyRegisteredException() {
        super("user has already registered");
    }
}
