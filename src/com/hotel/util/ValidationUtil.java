package com.hotel.util;

import java.util.regex.Pattern;

public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9]{10,15}$");

    public static boolean isValidEmail(String email) {
        return email != null && !email.trim().isEmpty() &&
                EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        String cleanPhone = phone.replaceAll("[\\s()-]", "");
        return PHONE_PATTERN.matcher(cleanPhone).matches();
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isPositive(double value) {
        return value > 0;
    }

    public static boolean isPositive(int value) {
        return value > 0;
    }

    public static String getEmailError(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Email is required";
        }
        if (!isValidEmail(email)) {
            return "Invalid email format";
        }
        return null;
    }

    public static String getPhoneError(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return "Phone number is required";
        }
        if (!isValidPhone(phone)) {
            return "Invalid phone number format (10-15 digits)";
        }
        return null;
    }

    public static String getRequiredFieldError(String fieldName, String value) {
        if (value == null || value.trim().isEmpty()) {
            return fieldName + " is required";
        }
        return null;
    }

    public static String getPositiveNumberError(String fieldName, double value) {
        if (value <= 0) {
            return fieldName + " must be greater than 0";
        }
        return null;
    }
}
