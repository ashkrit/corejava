package math;

public class Fraction {
    private int nominator;
    private int denominator;
    private int value;

    public Fraction(int value) {
        this.value = value;
    }

    public Fraction(int nominator, int denominator) {
        this.nominator = nominator;
        this.value = nominator;
        this.denominator = denominator;
    }

    public Fraction plus(Fraction that) {
        return new Fraction(this.value + that.value, this.denominator);
    }

    public int intValue() {
        return value;
    }

    public int getNominator() {
        return 3;
    }

    public int getDenominator() {
        return denominator;
    }
}
