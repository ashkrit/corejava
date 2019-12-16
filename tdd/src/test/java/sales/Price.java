package sales;

public class Price {
    private final int cents;

    public Price(int cents) {
        this.cents = cents;
    }

    public static Price cents(int cents) {
        return new Price(cents);
    }

    public double toDollar() {
        return this.cents / 100d;
    }

    @Override
    public String toString() {
        return String.valueOf(cents);
    }
}
