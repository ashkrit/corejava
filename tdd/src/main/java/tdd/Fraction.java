package tdd;

public class Fraction {
    private int nominator;
    private int denominator;
    private int value;

    public Fraction(int value) {
        this.value = value;
    }

    public Fraction(int nominator, int denominator) {
        this.nominator = nominator;
        this.denominator = denominator;
    }

    public Fraction plus(Fraction fraction) {
        if (fraction.denominator > 1) {
            return new Fraction(fraction.nominator + this.nominator, this.denominator);
        } else {
            return new Fraction(value + fraction.value);
        }
    }

    public int intValue() {
        return value;
    }

    public int denominator() {
        return denominator;
    }

    public int nominator() {
        return nominator;
    }
}
