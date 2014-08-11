package org.lastrix.expressioncalculator;

import java.util.Stack;

public enum Operation {
    ADD(1, false, false),
    SUB(1, false, false),
    DIV(2, false, false),
    MUL(2, false, false),
    BR_OPEN(4, false, true),
    BR_CLOSE(4, false, true),
    SIN(3, true, false),
    COS(3, true, false),
    EXP(3, true, false);

    private final int priority;
    private final boolean function;
    private final boolean allowedSequence;

    Operation(int priority, boolean function, boolean allowedSequence) {
        this.priority = priority;
        this.function = function;
        this.allowedSequence = allowedSequence;
    }

    /**
     * Convert string to Operation. This method returns null if no operation found.
     *
     * @param value -- string representation
     * @return Operation or null
     */
    public static Operation fromString(String value) {
        switch (value) {
            case "+":
                return ADD;
            case "-":
                return SUB;
            case "*":
                return MUL;
            case "/":
                return DIV;
            case "(":
                return BR_OPEN;
            case ")":
                return BR_CLOSE;
            case "sin":
                return SIN;
            case "cos":
                return COS;
            case "exp":
                return EXP;
            default:
                return null;
        }
    }

    public int getPriority() {
        return priority;
    }

    public boolean isFunction() {
        return function;
    }

    public boolean isAllowedSequence() {
        return allowedSequence;
    }

    public void operate(Stack<Number> stack) {
        if (!this.isFunction()) {
            final double second = stack.pop().doubleValue();
            final double first = stack.pop().doubleValue();
            switch (this) {
                case ADD:
                    stack.push(first + second);
                    break;

                case SUB:
                    stack.push(first - second);
                    break;

                case MUL:
                    stack.push(first * second);
                    break;

                case DIV:
                    stack.push(first / second);
                    break;

                default:
                    throw new IllegalStateException("Bad hands.");
            }
        } else {
            final double value = stack.pop().doubleValue();
            switch (this) {
                case SIN:
                    stack.push(Math.sin(value));
                    break;

                case COS:
                    stack.push(Math.cos(value));
                    break;

                case EXP:
                    stack.push(Math.exp(value));
                    break;

                default:
                    throw new IllegalStateException("Bad hands.");
            }
        }
    }
}
