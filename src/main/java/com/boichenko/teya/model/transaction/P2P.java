package com.boichenko.teya.model.transaction;

import com.boichenko.teya.model.UserID;

import java.math.BigDecimal;

public record P2P(UserID from, UserID to, BigDecimal amount) implements Transaction {

    @Override
    public Type type() {
        return Type.P2P;
    }
}
