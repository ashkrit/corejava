package tdd;

public class Fraction {
    private final int value;

    public Fraction(int value) {
        this.value = value;
    }

    public Fraction plus(Fraction fraction) {
        return this;
    }

    public int intValue() {
        return value;
    }
}
