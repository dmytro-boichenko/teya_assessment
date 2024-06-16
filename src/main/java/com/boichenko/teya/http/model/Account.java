package com.boichenko.teya.http.model;

import java.util.List;

public record Account(List<Transaction> transactions, String balance) {
}
