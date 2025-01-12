package com.yourcompany.rentalmanagement;
/**
 * @author FTech
 */
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SecretKeyGen {
    public static void main(String[] args) {
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String secretString = Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.println("JWT_SECRET=" + secretString);
    }
}
