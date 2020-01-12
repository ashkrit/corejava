package tdd;

public class Fraction {
    private int nominator;
    private int denominator;


    public Fraction(int value) {
        this(value, 1);
    }

    public Fraction(int nominator, int denominator) {
        this.nominator = nominator;
        this.denominator = denominator;
    }

    public Fraction plus(Fraction fraction) {
        if (fraction.denominator > 1) {
            return new Fraction(fraction.nominator + this.nominator, this.denominator);
        } else {
            return new Fraction(fraction.nominator + this.nominator);
        }
    }

    public int intValue() {
        return nominator;
    }

    public int denominator() {
        return denominator;
    }

    public int nominator() {
        return nominator;
    }
}
