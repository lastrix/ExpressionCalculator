package org.lastrix.expressioncalculator;

import java.util.EmptyStackException;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

@SuppressWarnings("WeakerAccess")
public class ExpressionCalculator implements Callable<Double> {
    private static final Pattern PATTERN = Pattern.compile("(?<=\\G(\\d+(?!\\d+)|\\w+(?!\\w+)|\\(|\\)|\\.|\\+|/|\\*|-))\\s*");

    private static final Pattern PATTERN_NUMBER = Pattern.compile("\\d+");

    private final String source;
    private final Stack<Operation> operations = new Stack<>();
    private final Stack<Number> stack = new Stack<>();
    private Operation previousOperation;
    private boolean number;
    private boolean fraction;
    private Operation operation;
    private Constant constant;

    public ExpressionCalculator(String source) {
        if (source == null) throw new NullPointerException("'source' == 'null'");
        if (source.length() == 0) throw new IllegalArgumentException("Empty string");
        this.source = source.toLowerCase();
    }

    @Override
    public Double call() throws Exception {
        String[] matches = PATTERN.split(source);
        int length = matches.length;

        previousOperation = null;
        fraction = false;

        try {
            for (int i = 0; i < length; i++) {
                number = PATTERN_NUMBER.matcher(matches[i]).matches();
                if (fraction || number) {
                    numberConstant(matches[i]);
                } else if (".".equals(matches[i])) {
                    fraction = true;
                } else if ((operation = Operation.fromString(matches[i])) != null) {
                    doOperation((i + 1 < length) ? matches[i + 1] : null);
                } else if ((constant = Constant.fromString(matches[i])) != null) {
                    constant();
                } else {
                    throw new CalculatorException(String.format("Unknown part '%s'", matches[i]));
                }
            }
        } catch (EmptyStackException e) {
            throw new CalculatorException("Stack underflow", e);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new CalculatorException("Incomplete function call", e);
        }

        //flush remaining operations
        while (operations.size() > 0) {
            apply(operations.pop());
        }

        if (stack.size() != 1) throw new CalculatorException("Failed to calculate");

        return stack.pop().doubleValue();
    }

    private void constant() {
        stack.push(constant.getValue());
        previousOperation = null;
    }

    private void doOperation(String next) throws CalculatorException {
        if (previousOperation == null || operation.isFunction() ||
                (previousOperation == operation && operation.isAllowedSequence()
                        || previousOperation.isAllowedSequence() ^ operation.isAllowedSequence())) {
            if (operation.isFunction() && Operation.fromString(next) != Operation.BR_OPEN)
                throw new CalculatorException("Function require open bracket after it");
            insert(operation);
            previousOperation = operation;
        } else {
            throw new CalculatorException("Sequential operators");
        }
    }

    private void numberConstant(String m) throws CalculatorException {
        if (fraction) {
            final Number previous = stack.pop();
            if (previousOperation != null || !number || previous instanceof Double)
                throw new CalculatorException("Failed to construct fractional value");
            stack.push(Double.parseDouble("0." + m) + previous.doubleValue());
            fraction = false;
        } else {
            stack.push(Integer.parseInt(m));
        }
        previousOperation = null;
    }

    private void apply(Operation operation) {
        operation.operate(stack);
    }

    private void insert(Operation operation) {
        if (operation == Operation.BR_CLOSE) {
            while (operations.peek() != Operation.BR_OPEN) {
                apply(operations.pop());
            }
            operations.pop();
        } else {
            while (operations.size() > 0) {
                final Operation o = operations.peek();
                if (o.getPriority() >= operation.getPriority()
                        && !(o.isFunction() && operation == Operation.BR_OPEN)
                        && o != Operation.BR_OPEN) {
                    operations.pop();
                    apply(o);
                } else {
                    break;
                }
            }
            operations.push(operation);
        }
    }

}
