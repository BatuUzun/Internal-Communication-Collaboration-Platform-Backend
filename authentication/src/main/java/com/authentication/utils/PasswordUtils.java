package com.authentication.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtils {
	

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * Hashes a plain-text password using BCrypt.
     *
     * @param password The plain-text password to hash.
     * @return The hashed password as a byte array.
     */
    public static byte[] hashPassword(String password) {
        String hashedPassword = encoder.encode(password);
        return hashedPassword.getBytes();
    }

    /**
     * Verifies a plain-text password against a hashed password.
     *
     * @param plainPassword The plain-text password.
     * @param hashedPassword The hashed password.
     * @return True if the passwords match; false otherwise.
     */
    public static boolean verifyPassword(String plainPassword, byte[] hashedPassword) {
        String hashedPasswordStr = new String(hashedPassword);
        return encoder.matches(plainPassword, hashedPasswordStr);
    }
}
