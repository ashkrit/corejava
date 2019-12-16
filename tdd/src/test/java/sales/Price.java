package sales;

public class Price {
    private final int cents;

    public Price(int cents) {
        this.cents = cents;
    }

    public static Price cents(int cents) {
        return new Price(cents);
    }

    @Override
    public String toString() {
        return String.valueOf(cents);
    }
}
