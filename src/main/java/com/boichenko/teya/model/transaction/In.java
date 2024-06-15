package com.boichenko.teya.model.transaction;

import com.boichenko.teya.model.UserID;

import java.math.BigDecimal;

public record In(UserID userId, BigDecimal amount) implements UserTransaction {

    @Override
    public Type type() {
        return Type.IN;
    }
}
