package math;

public class Fraction {
    private final int value;

    public Fraction(int value) {
        this.value = value;
    }

    public Fraction plus(Fraction that) {
        if (that.value != 0)
            return new Fraction(this.value + that.value);
        else
            return this;
    }

    public int intValue() {
        return value;
    }
}
