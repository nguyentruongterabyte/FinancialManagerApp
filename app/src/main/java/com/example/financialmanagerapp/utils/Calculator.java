package com.example.financialmanagerapp.utils;

import java.util.ArrayList;
import java.util.List;

public class Calculator {
    protected List<Double> numbers;
    protected List<String> mathOperators;

    public Calculator(List<Double> numbers, List<String> mathOperators) {
        this.numbers = numbers;
        this.mathOperators = mathOperators;
    }

    public double calculateExpression() {
        if (this.numbers.size() == 0) {
            throw new IllegalArgumentException("Arrays must not be empty");
        }

        // First pass: Handle multiplication and division
        List<Double> intermediateNumbers = new ArrayList<>();
        List<String> intermediateOperators = new ArrayList<>();

        double current = numbers.get(0);
        for (int i = 0; i < mathOperators.size(); i++) {
            String operator = mathOperators.get(i);
            double nextNumber = numbers.get(i + 1);

            switch (operator) {
                case "x":
                case "*":
                    current *= nextNumber;
                    break;
                case "/":
                case "÷":
                    if (nextNumber == 0)
                        throw new ArithmeticException("Division by zero error");
                    current /= nextNumber;
                    break;
                case "+":
                case "-":
                    intermediateNumbers.add(current);
                    intermediateOperators.add(operator);
                    current = nextNumber;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid operator '" + operator + "'");
            }
        }

        intermediateNumbers.add(current);

        // second pass: Handle addition and subtraction
        double result = intermediateNumbers.get(0);
        for (int i = 0; i < intermediateOperators.size(); i++) {
            String operator = intermediateOperators.get(i);
            double nextNumber = intermediateNumbers.get(i + 1);

            switch (operator) {
                case "+":
                    result += nextNumber;
                    break;
                case "-":
                    result -= nextNumber;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid operator '" + operator + "'");
            }
        }

        return result;
    }
}
