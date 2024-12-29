package com.yourcompany.rentalmanagement.util;

import com.yourcompany.rentalmanagement.model.User;

public class UserSession {

    private static UserSession instance;
    private User currentUser;
    private String token;

    private UserSession() {
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setCurrentUser(User user, String token) {
        this.currentUser = user;
        this.token = token;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public String getToken() {
        return token;
    }

    public void clearSession() {
        currentUser = null;
        token = null;
    }
}
