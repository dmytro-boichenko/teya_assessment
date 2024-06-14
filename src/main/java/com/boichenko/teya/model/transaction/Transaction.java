package com.boichenko.teya.model.transaction;

import java.math.BigDecimal;

public interface Transaction {
    Type type();

    BigDecimal amount();
}

