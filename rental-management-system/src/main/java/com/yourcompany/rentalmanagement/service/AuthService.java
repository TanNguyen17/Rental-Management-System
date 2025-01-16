package com.yourcompany.rentalmanagement.service;
/**
 * @author FTech
 */

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import io.github.cdimascio.dotenv.Dotenv;
import org.hibernate.Session;
import org.hibernate.query.Query;

import com.yourcompany.rentalmanagement.model.Host;
import com.yourcompany.rentalmanagement.model.Manager;
import com.yourcompany.rentalmanagement.model.Owner;
import com.yourcompany.rentalmanagement.model.Tenant;
import com.yourcompany.rentalmanagement.model.User;
import com.yourcompany.rentalmanagement.util.HibernateUtil;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthService {
    private static Dotenv dotenv = Dotenv.load();
    private static final String secret = dotenv.get("JWT_SECRET");
    private static byte[] decodedKey = Base64.getDecoder().decode(secret);
    //    private static final SecretKey JWT_SECRET = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final SecretKey JWT_SECRET = Keys.hmacShaKeyFor(decodedKey);
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    public User authenticateUser(String username, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Try to find user in each role table
            User user = findUserByUsername(session, Tenant.class, username);
            if (user == null) {
                user = findUserByUsername(session, Host.class, username);
            }
            if (user == null) {
                user = findUserByUsername(session, Owner.class, username);
            }
            if (user == null) {
                user = findUserByUsername(session, Manager.class, username);
            }

            if (user != null && user.checkPassword(password)) {
                System.out.println("Authentication successful for user: " + username);
                return user;
            }
        } catch (Exception e) {
            System.err.println("Authentication error: " + e.getMessage());
        }
        System.out.println("Authentication failed for user: " + username);
        return null;
    }

    private <T extends User> T findUserByUsername(Session session, Class<T> userClass, String username) {
        Query<T> query = session.createQuery(
                "FROM " + userClass.getSimpleName() + " WHERE username = :username ",
                userClass
        );
        query.setParameter("username", username);
        return query.uniqueResult();
    }

    public String generateToken(User user) {
        System.out.println(JWT_SECRET);
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("username", user.getUsername())
                .claim("role", user.getRole().name())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(24, ChronoUnit.HOURS)))
                .signWith(JWT_SECRET)
                .compact();
    }

    public User registerUser(String username, String password, String email, User.UserRole role) {
        System.out.println("Attempting to register user: " + username + " with role: " + role);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Check if username already exists
            if (authenticateUser(username, password) != null) {
                System.out.println("Username already exists: " + username);
                throw new RuntimeException("Username already exists");
            }

            User newUser;
            switch (role) {
                case TENANT -> {
                    System.out.println("Creating new Tenant");
                    newUser = new Tenant();
                }
                case HOST -> {
                    System.out.println("Creating new Host");
                    newUser = new Host();
                }
                case OWNER -> {
                    System.out.println("Creating new Owner");
                    newUser = new Owner();
                }
                case MANAGER -> {
                    System.out.println("Creating new Manager");
                    newUser = new Manager();
                }
                default ->
                        throw new RuntimeException("Invalid role");
            }

            newUser.setUsername(username);
            newUser.setHashedPassword(password);
            newUser.setEmail(email);
            newUser.setRole(role);

            System.out.println("Persisting new user to database...");
            session.persist(newUser);
            session.getTransaction().commit();
            System.out.println("Successfully created user with ID: " + newUser.getId());

            return newUser;
        } catch (Exception e) {
            System.err.println("Error during registration: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public boolean validateToken(String token) {
        System.out.println("Attempting to validate token: " + JWT_SECRET);
        try {
            Jwts.parserBuilder()
                    .setSigningKey(JWT_SECRET)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public User getUserFromToken(String token) {
        try {
            var claims = Jwts.parserBuilder()
                    .setSigningKey(JWT_SECRET)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.get("username", String.class);
            User.UserRole role = User.UserRole.valueOf(claims.get("role", String.class));

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                // Find user based on role
                Class<? extends User> userClass = switch (role) {
                    case TENANT ->
                            Tenant.class;
                    case HOST ->
                            Host.class;
                    case OWNER ->
                            Owner.class;
                    case MANAGER ->
                            Manager.class;
                    default ->
                            throw new RuntimeException("Invalid role in token");
                };
                return findUserByUsername(session, userClass, username);
            }
        } catch (Exception e) {
            System.err.println("Error getting user from token: " + e.getMessage());
            return null;
        }
    }
}