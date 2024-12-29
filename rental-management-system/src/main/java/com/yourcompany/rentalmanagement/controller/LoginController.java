package com.yourcompany.rentalmanagement.controller;

import com.yourcompany.rentalmanagement.model.User;
import com.yourcompany.rentalmanagement.model.UserRole;
import com.yourcompany.rentalmanagement.service.AuthService;
import com.yourcompany.rentalmanagement.util.UserSession;
import com.yourcompany.rentalmanagement.view.LoginViewController;

public class LoginController {

    private final AuthService authService = new AuthService();
    private final LoginViewController viewController;

    public LoginController(LoginViewController viewController) {
        this.viewController = viewController;
    }

    public void handleLogin(String username, String password) {
        try {
            User user = authService.authenticateUser(username, password);
            if (user != null) {
                String token = authService.generateToken(user);
                UserSession.getInstance().setCurrentUser(user, token);
                System.out.println("Successfully logged in as " + user.getRole()
                        + " with username: " + username);
                viewController.navigateToMainView();
            } else {
                viewController.showErrorMessage("Invalid credentials");
            }
        } catch (Exception e) {
            viewController.showErrorMessage("Login failed: " + e.getMessage());
        }
    }

    public void handleSignup(String username, String password, String email, UserRole role) {
        try {
            User user = authService.registerUser(username, password, email, role);
            String token = authService.generateToken(user);
            UserSession.getInstance().setCurrentUser(user, token);
            System.out.println("Successfully signed up as " + role
                    + " with username: " + username);
            viewController.navigateToMainView();
        } catch (Exception e) {
            viewController.showErrorMessage("Registration failed: " + e.getMessage());
        }
    }

    public void handleVisitor() {
        System.out.println("Continuing as visitor");
        viewController.navigateToMainView();
    }
}
