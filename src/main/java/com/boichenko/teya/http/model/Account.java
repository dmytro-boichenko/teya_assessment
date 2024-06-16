package com.boichenko.teya.http.model;

import java.util.List;

public record Account(int userID, List<Transaction> transactions, String balance) {
}
