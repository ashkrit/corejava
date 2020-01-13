package tdd;

public class Fraction {
    private final int nominator;
    private final int denominator;


    public Fraction(int value) {
        this(value, 1);
    }

    public Fraction(int nominator, int denominator) {
        this.nominator = nominator;
        this.denominator = denominator;
    }

    public Fraction plus(Fraction fraction) {
        return new Fraction(fraction.nominator + this.nominator, this.denominator);
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


    @Override
    public boolean equals(Object other) {
        if (other instanceof Fraction) {
            Fraction that = (Fraction) other;

            return this.nominator == that.nominator && this.denominator == that.denominator;
        }
        return false;
    }
}
