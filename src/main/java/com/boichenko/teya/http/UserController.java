package com.boichenko.teya.http;

import com.boichenko.teya.application.BookKeeper;
import com.boichenko.teya.http.model.*;
import com.boichenko.teya.model.UserID;
import com.boichenko.teya.model.transaction.In;
import com.boichenko.teya.model.transaction.Out;
import com.boichenko.teya.model.transaction.P2P;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

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
        com.boichenko.teya.model.User user = bookKeeper.user(new UserID(id));
        return ResponseEntity.ok(
                new User(
                        user.getFirstName(),
                        user.getLastName(),
                        user.isActive()
                ));
    }

    @RequestMapping(value = "/{id}/account", method = RequestMethod.GET)
    public ResponseEntity<Account> account(@PathVariable("id") int id) {
        com.boichenko.teya.model.Account account = bookKeeper.account(new UserID(id));
        List<Transaction> transactions = account.transactions().stream()
                .map(t -> {
                    switch (t.type()) {
                        case IN, OUT -> {
                            com.boichenko.teya.model.transaction.UserTransaction ut = (com.boichenko.teya.model.transaction.UserTransaction) t;
                            return new UserTransaction(
                                    ut.type().name(),
                                    ut.amount().toString(),
                                    ut.userId().id()
                            );
                        }
                        case P2P -> {
                            P2P p2p = (P2P) t;
                            return new P2PTransaction(
                                    p2p.type().name(),
                                    p2p.amount().toString(),
                                    p2p.from().id(),
                                    p2p.to().id()
                            );
                        }
                    }
                    throw new UnsupportedOperationException("transaction type is not supported");
                })
                .toList();

        return ResponseEntity.ok(
                new Account(
                        account.userID().id(),
                        transactions,
                        account.balance().toString())
        );
    }

    @RequestMapping(value = "/{id}/activate", method = RequestMethod.PUT)
    public void activateUser(@PathVariable("id") int id) {
        bookKeeper.activateUser(new UserID(id));
    }

    @RequestMapping(value = "/{id}/deactivate", method = RequestMethod.PUT)
    public void deactivateUser(@PathVariable("id") int id) {
        bookKeeper.deactivateUser(new UserID(id));
    }

    @RequestMapping(value = "/{id}/topup", method = RequestMethod.POST)
    public void accountTopUp(@PathVariable("id") int id, @RequestBody UserTransaction t) {
        bookKeeper.saveTransaction(new In(new UserID(id), new BigDecimal(t.amount())));
    }

    @RequestMapping(value = "/{id}/withdraw", method = RequestMethod.POST)
    public void accountWithdraw(@PathVariable("id") int id, @RequestBody UserTransaction t) {
        bookKeeper.saveTransaction(new Out(new UserID(id), new BigDecimal(t.amount())));
    }

    @RequestMapping(value = "/{id}/p2p", method = RequestMethod.POST)
    public void accountP2P(@PathVariable("id") int id, @RequestBody P2PTransaction t) {
        bookKeeper.saveTransaction(new P2P(
                new UserID(id),
                new UserID(t.toUserID()),
                new BigDecimal(t.amount())
        ));
    }

}
