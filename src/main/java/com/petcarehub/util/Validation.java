package com.petcarehub.util;

import com.petcarehub.exception.ValidationException;

public final class Validation {
    private Validation() {}

    public static String required(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(field + " is required.");
        }
        return value.trim();
    }

    public static int positiveInt(String value, String field) {
        try {
            int parsed = Integer.parseInt(required(value, field));
            if (parsed <= 0) throw new ValidationException(field + " must be greater than 0.");
            return parsed;
        } catch (NumberFormatException ex) {
            throw new ValidationException(field + " must be a whole number.");
        }
    }

    public static double nonNegativeDouble(String value, String field) {
        try {
            double parsed = Double.parseDouble(required(value, field));
            if (parsed < 0) throw new ValidationException(field + " cannot be negative.");
            return parsed;
        } catch (NumberFormatException ex) {
            throw new ValidationException(field + " must be numeric.");
        }
    }

    public static void strongEnoughPassword(String password) {
        if (password == null || password.length() < 6) {
            throw new ValidationException("Password must contain at least 6 characters.");
        }
    }
}
