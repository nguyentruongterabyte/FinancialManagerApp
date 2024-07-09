package com.example.financialmanagerapp.utils;

import com.example.financialmanagerapp.model.Currency;
import com.example.financialmanagerapp.model.User;

import java.util.ArrayList;

import java.util.List;

public class Utils {
    // Base URL
    public static final String BASE_URL = "http://192.168.1.16:6789/";

    // Currencies
    public static List<Currency> currencies = new ArrayList<>();

    // Current user
    public static User currentUser = null;
}
