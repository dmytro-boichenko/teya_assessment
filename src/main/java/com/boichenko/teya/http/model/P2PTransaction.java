package com.boichenko.teya.http.model;

public class P2PTransaction extends Transaction {

    private final int fromUserID;
    private final int toUserID;


    public P2PTransaction(String type, String amount, int fromUserID, int toUserID) {
        super(type, amount);
        this.fromUserID = fromUserID;
        this.toUserID = toUserID;
    }

    public int fromUserID() {
        return fromUserID;
    }

    public int toUserID() {
        return toUserID;
    }
}
