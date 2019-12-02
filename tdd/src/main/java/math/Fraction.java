package math;

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

    public Fraction plus(Fraction that) {
        return new Fraction(this.nominator + that.nominator, this.denominator);
    }

    @Override
    public int hashCode() {
        return nominator * 37 + denominator;
    }

    @Override
    public boolean equals(Object that) {
        if (that instanceof Fraction) {
            Fraction thatValue = (Fraction) that;
            return thatValue.nominator == this.nominator && thatValue.denominator == this.denominator;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s/%s", nominator, denominator);
    }
}
