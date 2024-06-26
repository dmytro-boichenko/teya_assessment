package com.boichenko.teya.model;

import com.boichenko.teya.model.exception.UserActiveException;
import com.boichenko.teya.model.exception.UserAlreadyRegisteredException;
import com.boichenko.teya.model.exception.UserNotActiveException;
import com.boichenko.teya.model.exception.UserNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class UsersKeeper {

    private final Map<UserID, User> usersMap;

    public UsersKeeper() {
        this.usersMap = new HashMap<>();
    }

    public UserID registerUser(String firstName, String lastName) {
        User user = new User(firstName, lastName);

        if (usersMap.containsValue(user)) {
            throw new UserAlreadyRegisteredException();
        }

        int id = usersMap.size() + 1;
        UserID userId = new UserID(id);
        usersMap.put(userId, user);

        return userId;
    }

    public User getUser(UserID userID) {
        User user = usersMap.get(userID);
        if (user == null) {
            throw new UserNotFoundException(userID);
        }

        return user;
    }

    public void activateUser(UserID userID) {
        User user = getUser(userID);

        if (user.isActive()) {
            throw new UserActiveException(userID);
        }

        user.setActive(true);
    }

    public void deactivateUser(UserID userID) {
        User user = getUser(userID);

        if (!user.isActive()) {
            throw new UserNotActiveException(userID);
        }

        user.setActive(false);
    }

    public boolean isUserActive(UserID userID) {
        return getUser(userID).isActive();
    }
}
