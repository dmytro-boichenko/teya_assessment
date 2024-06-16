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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        TransactionToUser that = (TransactionToUser) o;

        return toUserID == that.toUserID;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + toUserID;
        return result;
    }
}
