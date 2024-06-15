package com.boichenko.teya.http;

import com.boichenko.teya.application.BookKeeper;
import com.boichenko.teya.http.model.UserIdentity;
import com.boichenko.teya.http.model.UserInfo;
import com.boichenko.teya.model.UserID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private final BookKeeper bookKeeper;

    public UserController(BookKeeper bookKeeper) {
        this.bookKeeper = bookKeeper;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<UserIdentity> registerUser(@RequestBody UserInfo userInfo) {
        UserID userID = bookKeeper.registerUser(userInfo.firstName(), userInfo.lastName());
        return ResponseEntity.ok(new UserIdentity(userID.id()));
    }

}
