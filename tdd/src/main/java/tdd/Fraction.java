package tdd;

public class Fraction {
    private final int value;

    public Fraction(int value) {
        this.value = value;
    }

    public Fraction plus(Fraction fraction) {
        if (value > 0 && fraction.value > 0) {
            return new Fraction(value + fraction.value);
        } else {
            if (fraction.value != 0)
                return new Fraction(value + fraction.value);
            else
                return new Fraction(value + fraction.value);
        }
    }

    public int intValue() {
        return value;
    }
}
