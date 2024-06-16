package com.boichenko.teya.http.model;

public class Transaction {

    private final String type;
    private final String amount;

    public Transaction(String type, String amount) {
        this.type = type;
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public String getAmount() {
        return amount;
    }
}