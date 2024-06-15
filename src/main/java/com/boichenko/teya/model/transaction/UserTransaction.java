package com.boichenko.teya.model.transaction;

import com.boichenko.teya.model.UserID;

public interface UserTransaction extends Transaction {
    UserID userId();
}
