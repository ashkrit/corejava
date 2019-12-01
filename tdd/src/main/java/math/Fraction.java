package math;

public class Fraction {
    private final int value;

    public Fraction(int value) {
        this.value = value;
    }

    public Fraction plus(Fraction that) {
        return this;
    }

    public int intValue() {
        return value;
    }
}
