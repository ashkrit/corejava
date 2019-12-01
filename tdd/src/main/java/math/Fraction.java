package math;

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

    public Fraction plus(Fraction that) {
        return new Fraction(this.nominator + that.nominator, this.denominator);
    }

    public int intValue() {
        return nominator;
    }

    public int getNominator() {
        return nominator;
    }

    public int getDenominator() {
        return denominator;
    }
}
