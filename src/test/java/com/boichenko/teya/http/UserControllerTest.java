package com.boichenko.teya.http;

import com.boichenko.teya.http.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    private enum transactionType {
        topup, withdraw, p2p
    }

    @LocalServerPort
    private int port;
    @Autowired
    private UserController controller;
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testCreateEmptyUser() {
        String firstName = "emptyFirstName";
        String lastName = "emptyLastName";

        ResponseEntity<UserIdentity> resp = createUser(firstName, lastName);
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        UserIdentity userIdentity = Objects.requireNonNull(resp.getBody());
        assertTrue(userIdentity.id() > 0);

        ResponseEntity<User> userResp = getUser(userIdentity.id());
        assertEquals(HttpStatus.OK, userResp.getStatusCode());
        User user = Objects.requireNonNull(userResp.getBody());
        assertTrue(user.active());
        assertEquals(firstName, user.firstName());
        assertEquals(lastName, user.lastName());

        ResponseEntity<Account> userAccountResp = getUserAccount(userIdentity.id());
        assertEquals(HttpStatus.OK, userAccountResp.getStatusCode());
        Account account = Objects.requireNonNull(userAccountResp.getBody());
        assertEquals("0", account.balance());
        assertEquals(0, account.transactions().size());
    }

    @Test
    void testCreateUserWithTransactions() {
        String firstName = "transactionFirstName";
        String lastName = "transactionLastName";

        ResponseEntity<UserIdentity> resp = createUser(firstName, lastName);
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        UserIdentity userIdentity = Objects.requireNonNull(resp.getBody());
        assertTrue(userIdentity.id() > 0);

        ResponseEntity<User> userResp = getUser(userIdentity.id());
        assertEquals(HttpStatus.OK, userResp.getStatusCode());
        User user = Objects.requireNonNull(userResp.getBody());
        assertTrue(user.active());
        assertEquals(firstName, user.firstName());
        assertEquals(lastName, user.lastName());

        addTransaction(userIdentity.id(), new Transaction("", "12.34"), transactionType.topup);

        ResponseEntity<Account> userAccountResp = getUserAccount(userIdentity.id());
        assertEquals(HttpStatus.OK, userAccountResp.getStatusCode());
        Account account = Objects.requireNonNull(userAccountResp.getBody());
        assertEquals("12.34", account.balance());
        assertEquals(List.of(new Transaction("IN", "12.34")), account.transactions());
    }

    @Test
    void testCreateUserWithWithdrawTransaction() {
        String firstName = "withdrawFirstName";
        String lastName = "withdrawLastName";

        ResponseEntity<UserIdentity> resp = createUser(firstName, lastName);
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        UserIdentity userIdentity = Objects.requireNonNull(resp.getBody());
        assertTrue(userIdentity.id() > 0);

        ResponseEntity<User> userResp = getUser(userIdentity.id());
        assertEquals(HttpStatus.OK, userResp.getStatusCode());
        User user = Objects.requireNonNull(userResp.getBody());
        assertTrue(user.active());
        assertEquals(firstName, user.firstName());
        assertEquals(lastName, user.lastName());

        ResponseEntity<Void> reply =
                addTransaction(userIdentity.id(), new Transaction("", "12.34"), transactionType.withdraw);
        assertEquals(HttpStatus.BAD_REQUEST, reply.getStatusCode());
    }

    @Test
    void testCreateUsersAndP2PTransactions() {
        UserIdentity firstUserID = Objects.requireNonNull(createUser("P2P_1_FirstName", "P2P_1_LastName").getBody());
        assertTrue(firstUserID.id() > 0);

        UserIdentity secondUserID = Objects.requireNonNull(createUser("P2P_2_FirstName", "P2P_2_LastName").getBody());
        assertTrue(secondUserID.id() > 0);

        addTransaction(firstUserID.id(), new Transaction("", "12.34"), transactionType.topup);
        addTransaction(firstUserID.id(), new TransactionToUser("", "10", secondUserID.id()), transactionType.p2p);

        Account firstUserAccount = Objects.requireNonNull(getUserAccount(firstUserID.id()).getBody());
        assertEquals("2.34", firstUserAccount.balance());
        assertEquals(List.of(
                new Transaction("IN", "12.34"),
                new Transaction("P2P", "10")
        ), firstUserAccount.transactions());

        Account secondUserAccount = Objects.requireNonNull(getUserAccount(secondUserID.id()).getBody());
        assertEquals("10", secondUserAccount.balance());
        assertEquals(List.of(
                new Transaction("P2P", "10")
        ), secondUserAccount.transactions());
    }

    private ResponseEntity<UserIdentity> createUser(String firstName, String lastName) {
        return this.restTemplate.postForEntity(
                "http://localhost:" + port + "/user/create",
                new UserInfo(firstName, lastName),
                UserIdentity.class
        );
    }

    private ResponseEntity<User> getUser(int id) {
        return this.restTemplate.getForEntity(
                "http://localhost:" + port + "/user/" + id,
                User.class
        );
    }

    private ResponseEntity<Account> getUserAccount(int id) {
        return this.restTemplate.getForEntity(
                "http://localhost:" + port + "/user/" + id + "/account",
                Account.class
        );
    }

    private ResponseEntity<Void> addTransaction(int userID, Transaction t, transactionType type) {
        return this.restTemplate.postForEntity(
                "http://localhost:" + port + "/user/" + userID + "/" + type.name(),
                t,
                Void.class
        );

    }

}
