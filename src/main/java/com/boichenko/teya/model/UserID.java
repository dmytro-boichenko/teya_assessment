package com.boichenko.teya.model;

public record UserID(int id) {

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                '}';
    }
}
