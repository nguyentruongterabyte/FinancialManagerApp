package com.example.financialmanagerapp.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator {
    // Regex pattern for validating email address
    private static final String EMAIL_PATTERN =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    // Method to validate email addresses
    public static boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


}
