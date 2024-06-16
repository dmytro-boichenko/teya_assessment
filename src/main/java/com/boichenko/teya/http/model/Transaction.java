package com.boichenko.teya.http.model;

public abstract class Transaction {
    private final String type;
    private final String amount;

    public Transaction(String type, String amount) {
        this.type = type;
        this.amount = amount;
    }

    public String type() {
        return type;
    }

    public String amount() {
        return amount;
    }
}