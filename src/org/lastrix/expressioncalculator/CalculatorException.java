package org.lastrix.expressioncalculator;

@SuppressWarnings("WeakerAccess")
public class CalculatorException extends Exception {
    public CalculatorException(String message) {
        super(message);
    }

    public CalculatorException(String message, Throwable cause) {
        super(message, cause);
    }
}
