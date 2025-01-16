//package org.example;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class RegisterAccountTest {
    private class RegistrationService {
        public String registerAccount(String username, String email, String password) {
            if (username == null || username.isEmpty() || email == null || email.isEmpty() || password == null || password.isEmpty()) {
                return "All fields are required.";
            }
            // Email constraints
            if (!isValidEmail(email)) {
                return "Invalid email format.";
            }

            // Password constraints
            // Length: At least 8 characters.
            // Character requirements:
            // At least one uppercase letter (A-Z).
            // At least one lowercase letter (a-z).
            // At least one number (0-9).
            // At least one special character (e.g., ! @ # $ % ^ & * ( ) _ + - = [ ] { } ; ' : " \ | , . < > / ?).
            if (password.length() < 8 || !password.matches(".*[A-Z].*") || !password.matches(".*\\d.*") || !password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
                return "Password does not meet strength requirements.";
            }

            if (username.equals("testuser1") || username.equals("newuser42")) { // Simulate existing users
                return "Username is already taken.";
            }
            return "Registration successful.";
        }

        private boolean isValidEmail(String email) {
            String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
            return email.matches(regex);
        }
    }

    private RegistrationService registrationService = new RegistrationService();

    // Verify handling of weak password
    @Test
    void testWeakPassword() {
        String result1 = registrationService.registerAccount("weakpasswordtest", "tannm2005@gmail.com", "password@2");
        String result2 = registrationService.registerAccount("veryweakuser", "tannm2005@gmail.com", "123456");
        assertEquals("Password does not meet strength requirements.", result1);
        assertEquals("Password does not meet strength requirements.", result2);
    }
}