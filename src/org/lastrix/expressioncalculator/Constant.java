package org.lastrix.expressioncalculator;


public enum Constant {
    PI(Math.PI),
    E(Math.E);

    private final double value;

    Constant(double value) {
        this.value = value;
    }

    /**
     * Convert string to Constant enum. This method returns null if no constant found
     *
     * @param value -- string representation
     * @return Constant or null
     */
    public static Constant fromString(String value) {
        switch (value) {
            case "pi":
                return PI;
            case "e":
                return E;
            default:
                return null;
        }
    }

    public double getValue() {
        return value;
    }
}
