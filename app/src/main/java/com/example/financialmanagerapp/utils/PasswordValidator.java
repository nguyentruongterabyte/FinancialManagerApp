package com.example.financialmanagerapp.utils;

import java.util.regex.Pattern;

public class PasswordValidator
{
    public static final String passwordPattern = "Minimum 8 characters, at least one uppercase letter, one lowercase letter, one number and one special character";
    // password pattern: Minimum 8 characters, at least one uppercase letter, one lowercase letter, one number and one special character
    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    public static boolean isMatched(String password, String passwordConfirmation) {
        return password.equals(passwordConfirmation);
    }

    public static boolean isValidPassword(String password) {
        return pattern.matcher(password).matches();
    }
}
