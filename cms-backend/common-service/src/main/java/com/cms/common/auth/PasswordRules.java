package com.cms.common.auth;

/**
 * Password strength rules for create / change password.
 * Min 8 characters, at least one uppercase, one lowercase, and one special character.
 */
public final class PasswordRules {

    public static final int MIN_LENGTH = 8;
    public static final int MAX_FAILED_ATTEMPTS = 3;
    public static final int LOCK_MINUTES = 5;

    private PasswordRules() {}

    /**
     * @throws IllegalArgumentException if password does not meet rules
     */
    public static void validateStrength(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (password.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("Password must be at least " + MIN_LENGTH + " characters");
        }
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasSpecial = false;
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (!Character.isLetterOrDigit(c) && !Character.isWhitespace(c)) hasSpecial = true;
        }
        if (!hasUpper) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }
        if (!hasLower) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter");
        }
        if (!hasSpecial) {
            throw new IllegalArgumentException("Password must contain at least one special character");
        }
    }
}
