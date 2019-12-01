package math;

public class Fraction {
    private final int value;

    public Fraction(int value) {
        this.value = value;
    }

    public Fraction plus(Fraction that) {
        return new Fraction(this.value + that.value);
    }

    public int intValue() {
        return value;
    }
}
