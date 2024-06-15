package com.boichenko.teya.http;

import com.boichenko.teya.application.BookKeeper;
import com.boichenko.teya.http.model.UserIdentity;
import com.boichenko.teya.http.model.UserInfo;
import com.boichenko.teya.model.Account;
import com.boichenko.teya.model.User;
import com.boichenko.teya.model.UserID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<User> user(@PathVariable("id") int id) {
        User user = bookKeeper.user(new UserID(id));
        return ResponseEntity.ok(user);
    }

    @RequestMapping(value = "/{id}/account", method = RequestMethod.GET)
    public ResponseEntity<Account> account(@PathVariable("id") int id) {
        Account account = bookKeeper.account(new UserID(id));
        return ResponseEntity.ok(account);
    }

    @RequestMapping(value = "/{id}/activate", method = RequestMethod.PUT)
    public void activateUser(@PathVariable("id") int id) {
        bookKeeper.activateUser(new UserID(id));
    }

    @RequestMapping(value = "/{id}/deactivate", method = RequestMethod.PUT)
    public void deactivateUser(@PathVariable("id") int id) {
        bookKeeper.deactivateUser(new UserID(id));
    }

}
