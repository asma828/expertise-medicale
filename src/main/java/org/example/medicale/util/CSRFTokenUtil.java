package org.example.medicale.util;
import java.security.SecureRandom;
import java.util.Base64;

public class CSRFTokenUtil {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final int TOKEN_LENGTH = 32;

    /**
     * Génère un token CSRF aléatoire
     */
    public static String generateToken() {
        byte[] randomBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    /**
     * Valide un token CSRF
     */
    public static boolean validateToken(String token, String sessionToken) {
        if (token == null || sessionToken == null) {
            return false;
        }
        return token.equals(sessionToken);
    }
}
