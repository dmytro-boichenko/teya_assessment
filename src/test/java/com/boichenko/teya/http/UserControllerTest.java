package com.boichenko.teya.http;

import com.boichenko.teya.http.model.Account;
import com.boichenko.teya.http.model.User;
import com.boichenko.teya.http.model.UserIdentity;
import com.boichenko.teya.http.model.UserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    @LocalServerPort
    private int port;
    @Autowired
    private UserController controller;
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testCreateEmptyUser() {
        String firstName = "John";
        String lastName = "Doe";
        ResponseEntity<UserIdentity> resp = createUser(firstName, lastName);
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        UserIdentity userIdentity = Objects.requireNonNull(resp.getBody());
        assertEquals(1, userIdentity.id());

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

}
