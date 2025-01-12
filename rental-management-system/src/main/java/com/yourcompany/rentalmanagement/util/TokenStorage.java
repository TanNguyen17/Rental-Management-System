package com.yourcompany.rentalmanagement.util;
/**
 * @author FTech
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class TokenStorage {

    private static final String TOKEN_FILE = System.getProperty("user.home") + File.separator + ".rental-management" + File.separator + "auth.properties";
    private static final Properties properties = new Properties();

    static {
        createTokenDirectory();
    }

    private static void createTokenDirectory() {
        try {
            File tokenDir = new File(TOKEN_FILE).getParentFile();
            if (!tokenDir.exists()) {
                tokenDir.mkdirs();
            }
        } catch (Exception e) {
            System.err.println("Error creating token directory: " + e.getMessage());
        }
    }

    public static void saveToken(String username, String token) {
        try {
            properties.setProperty("username", username);
            properties.setProperty("token", token);
            properties.setProperty("timestamp", String.valueOf(System.currentTimeMillis()));

            try (FileOutputStream out = new FileOutputStream(TOKEN_FILE)) {
                properties.store(out, "Authentication Token");
            }
            System.out.println("Token saved successfully");
        } catch (IOException e) {
            System.err.println("Error saving token: " + e.getMessage());
        }
    }

    public static String loadToken() {
        try {
            if (!new File(TOKEN_FILE).exists()) {
                return null;
            }

            try (FileInputStream in = new FileInputStream(TOKEN_FILE)) {
                properties.load(in);
                String token = properties.getProperty("token");
                // Check if token is expired (24 hours)
                long timestamp = Long.parseLong(properties.getProperty("timestamp", "0"));
                if (System.currentTimeMillis() - timestamp > 24 * 60 * 60 * 1000) {
                    clearToken();
                    return null;
                }
                return token;
            }
        } catch (IOException e) {
            System.err.println("Error loading token: " + e.getMessage());
            return null;
        }
    }

    public static void clearToken() {
        try {
            Files.deleteIfExists(Paths.get(TOKEN_FILE));
            System.out.println("Token cleared successfully");
        } catch (IOException e) {
            System.err.println("Error clearing token: " + e.getMessage());
        }
    }
}
