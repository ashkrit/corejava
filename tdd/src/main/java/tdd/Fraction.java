package tdd;

public class Fraction {
    private final int value;

    public Fraction(int value) {
        this.value = value;
    }

    public Fraction plus(Fraction fraction) {
        return new Fraction(value + fraction.value);
    }

    public int intValue() {
        return value;
    }
}
