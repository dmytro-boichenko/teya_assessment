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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transaction that = (Transaction) o;

        if (!type.equals(that.type)) return false;
        return amount.equals(that.amount);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + amount.hashCode();
        return result;
    }
}