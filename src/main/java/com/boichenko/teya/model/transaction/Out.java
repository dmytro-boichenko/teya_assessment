package com.boichenko.teya.model.transaction;

import com.boichenko.teya.model.User;

import java.math.BigDecimal;

public record Out(User from, BigDecimal amount) implements Transaction {

    @Override
    public Type type() {
        return Type.OUT;
    }
}
