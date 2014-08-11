package org.lastrix.expressioncalculator;

public class Main {

    public static void main(String[] args) {
        for (String arg : args) {
            try {
                System.out.println(String.format("%20.5f <--- %s", new ExpressionCalculator(arg).call(), arg));
            } catch (Exception e) {
                System.out.println(String.format("*** Exception: %s for input '%s'", e.getMessage(), arg));
            }
            System.out.println("---------------------------------------------------------------------------------");
        }
    }
}
