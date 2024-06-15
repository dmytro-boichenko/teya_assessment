package com.boichenko.teya.model;

import com.boichenko.teya.model.exception.UserActiveException;
import com.boichenko.teya.model.exception.UserAlreadyRegisteredException;
import com.boichenko.teya.model.exception.UserNotActiveException;
import com.boichenko.teya.model.exception.UserNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class UsersKeeper {

    private final Map<UserID, User> usersMap;
    private int usersCount;

    public UsersKeeper() {
        this.usersMap = new HashMap<>();
        this.usersCount = 0;
    }

    public UserID registerUser(String firstName, String lastName) {
        User user = new User(firstName, lastName);

        if (usersMap.containsValue(user)) {
            throw new UserAlreadyRegisteredException();
        }

        this.usersCount++;
        UserID userId = new UserID(this.usersCount);
        usersMap.put(userId, user);

        return userId;
    }

    public void activateUser(UserID userID) {
        User user = usersMap.get(userID);
        if (user == null) {
            throw new UserNotFoundException();
        }

        if (user.isActive()) {
            throw new UserActiveException();
        }

        user.setActive(true);
    }

    public void deactivateUser(UserID userID) {
        User user = usersMap.get(userID);
        if (user == null) {
            throw new UserNotFoundException();
        }

        if (!user.isActive()) {
            throw new UserNotActiveException();
        }

        user.setActive(false);
    }

    public boolean isUserActive(UserID userID) {
        User user = usersMap.get(userID);
        if (user == null) {
            throw new UserNotFoundException();
        }

        return user.isActive();
    }
}
