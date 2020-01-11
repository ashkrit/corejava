package tdd;

public class Fraction {
    private final int value;

    public Fraction(int value) {
        this.value = value;
    }

    public Fraction plus(Fraction fraction) {
        if (fraction.value != 0)
            return fraction;
        else
            return this;
    }

    public int intValue() {
        return value;
    }
}
