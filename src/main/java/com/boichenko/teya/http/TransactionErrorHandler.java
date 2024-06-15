package com.boichenko.teya.http;

import com.boichenko.teya.http.model.Error;
import com.boichenko.teya.model.exception.NegativeOrZeroTransactionAmountException;
import com.boichenko.teya.model.exception.NotEnoughMoneyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class TransactionErrorHandler {

    @ExceptionHandler(NegativeOrZeroTransactionAmountException.class)
    public ResponseEntity<Error> handleNegativeOrZeroTransactionAmountException() {
        return new ResponseEntity<>(new Error("transaction amount is negative or zero"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotEnoughMoneyException.class)
    public ResponseEntity<Error> handleNotEnoughMoneyException(NotEnoughMoneyException ex) {
        return new ResponseEntity<>(new Error("user " + ex.userID() + " doesn't have enough money for making transaction"), HttpStatus.BAD_REQUEST);
    }
}
