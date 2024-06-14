package com.boichenko.teya.model.transaction;

import com.boichenko.teya.model.User;

import java.math.BigDecimal;

public record P2P(User from, User to, BigDecimal amount) implements Transaction {

    @Override
    public Type type() {
        return Type.P2P;
    }
}
