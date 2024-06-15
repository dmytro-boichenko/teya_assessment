package com.boichenko.teya.http;

import com.boichenko.teya.http.model.Error;
import com.boichenko.teya.model.exception.UserActiveException;
import com.boichenko.teya.model.exception.UserAlreadyRegisteredException;
import com.boichenko.teya.model.exception.UserNotActiveException;
import com.boichenko.teya.model.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserErrorHandler {

    @ExceptionHandler(UserNotActiveException.class)
    public ResponseEntity<Error> handleUserNotActiveException(UserNotActiveException ex) {
        return new ResponseEntity<>(new Error("user " + ex.userID() + " is not active"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserActiveException.class)
    public ResponseEntity<Error> handleUserActiveException(UserActiveException ex) {
        return new ResponseEntity<>(new Error("user " + ex.userID() + " is active"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Error> handleUserNotFoundException(UserNotFoundException ex) {
        return new ResponseEntity<>(new Error("user " + ex.userID() + " not found"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyRegisteredException.class)
    public ResponseEntity<Error> handleUserAlreadyRegisteredException() {
        return ResponseEntity.badRequest().body(new Error("user with such first and last names already registered"));
    }
}
