package com.example.financialmanagerapp.utils;

import java.text.DecimalFormat;

public class MoneyFormatter {
    public static String getText(String symbol, Double amount) {
        DecimalFormat dft = new DecimalFormat("#,###.##");

        try {
            if (amount >= 0)
                return String.format("%s %s", symbol, dft.format(amount));
            return String.format("-%s %s", symbol, dft.format(Math.abs(amount)));
        } catch (NumberFormatException e) {
            return symbol + " " + amount;
        }
    }
}
