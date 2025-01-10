package com.yourcompany.rentalmanagement.util;

import com.yourcompany.rentalmanagement.model.User;
import com.yourcompany.rentalmanagement.service.AuthService;

public class UserSession {

    private static UserSession instance;
    private User currentUser;
    private String token;

    private UserSession() {
        // Try to load existing token on startup
        loadStoredSession();
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
        // Save token to file
        if (user != null && token != null) {
            TokenStorage.saveToken(user.getUsername(), token);
        }
    }

    private void loadStoredSession() {
        String storedToken = TokenStorage.loadToken();
        System.out.println("Stored token: " + storedToken);
        if (storedToken != null) {
            // Validate token and load user
            AuthService authService = new AuthService();
            if (authService.validateToken(storedToken)) {
                this.token = storedToken;
                // Load user details from token
                this.currentUser = authService.getUserFromToken(storedToken);
            } else {
                TokenStorage.clearToken();
            }
        }
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
        TokenStorage.clearToken();
    }
}
