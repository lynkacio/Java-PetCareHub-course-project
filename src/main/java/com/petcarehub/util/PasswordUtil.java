package com.petcarehub.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public final class PasswordUtil {
    private static final int ITERATIONS = 65_536;
    private static final int KEY_LENGTH = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {}

    public static String hash(String password) {
        try {
            byte[] salt = new byte[16];
            RANDOM.nextBytes(salt);
            byte[] encoded = pbkdf2(password.toCharArray(), salt, ITERATIONS);
            return "pbkdf2$" + ITERATIONS + "$" + Base64.getEncoder().encodeToString(salt)
                    + "$" + Base64.getEncoder().encodeToString(encoded);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to hash password", ex);
        }
    }

    public static boolean verify(String password, String stored) {
        if (stored == null) return false;
        if (!stored.startsWith("pbkdf2$")) {
            return stored.equals(password); // backward compatibility with the original lab data
        }
        try {
            String[] parts = stored.split("\\$");
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            byte[] actual = pbkdf2(password.toCharArray(), salt, iterations);
            if (actual.length != expected.length) return false;
            int diff = 0;
            for (int i = 0; i < actual.length; i++) diff |= actual[i] ^ expected[i];
            return diff == 0;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isHashed(String stored) {
        return stored != null && stored.startsWith("pbkdf2$");
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, KEY_LENGTH);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } finally {
            spec.clearPassword();
        }
    }
}
