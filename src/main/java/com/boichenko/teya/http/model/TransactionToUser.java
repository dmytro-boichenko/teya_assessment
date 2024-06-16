package com.boichenko.teya.http.model;

public class TransactionToUser extends Transaction {

    private final int toUserID;

    public TransactionToUser(String type, String amount, int toUserID) {
        super(type, amount);
        this.toUserID = toUserID;
    }

    public int getToUserID() {
        return toUserID;
    }
}
