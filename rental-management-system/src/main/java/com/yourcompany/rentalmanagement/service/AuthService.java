package com.yourcompany.rentalmanagement.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.crypto.SecretKey;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.yourcompany.rentalmanagement.model.Host;
import com.yourcompany.rentalmanagement.model.Manager;
import com.yourcompany.rentalmanagement.model.Owner;
import com.yourcompany.rentalmanagement.model.Tenant;
import com.yourcompany.rentalmanagement.model.User;
import com.yourcompany.rentalmanagement.model.UserRole;
import com.yourcompany.rentalmanagement.util.HibernateUtil;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class AuthService {

    private static final SecretKey JWT_SECRET = Keys.secretKeyFor(SignatureAlgorithm.HS256);

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
                "FROM " + userClass.getSimpleName() + " WHERE username = :username",
                userClass
        );
        query.setParameter("username", username);
        return query.uniqueResult();
    }

    public String generateToken(User user) {
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

    public User registerUser(String username, String password, String email, UserRole role) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Check if username already exists
            if (authenticateUser(username, password) != null) {
                throw new RuntimeException("Username already exists");
            }

            User newUser;
            switch (role) {
                case TENANT ->
                    newUser = new Tenant();
                case HOST ->
                    newUser = new Host();
                case OWNER ->
                    newUser = new Owner();
                case MANAGER ->
                    newUser = new Manager();
                default ->
                    throw new RuntimeException("Invalid role");
            }

            newUser.setUsername(username);
            newUser.setHashedPassword(password);
            newUser.setEmail(email);
            newUser.setRole(role);

            session.persist(newUser);
            session.getTransaction().commit();

            System.out.println("Successfully created new user: " + username + " with role: " + role);
            return newUser;
        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            throw e;
        }
    }
}
