package com.boichenko.teya.http.model;

public class UserTransaction extends Transaction {

    private final int userID;

    public UserTransaction(String type, String amount, int userID) {
        super(type, amount);
        this.userID = userID;
    }

    public int userID() {
        return userID;
    }
}
