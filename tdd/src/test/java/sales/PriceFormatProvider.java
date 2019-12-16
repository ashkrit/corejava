package sales;

public class PriceFormatProvider {
    public static String format(Price price) {
        return String.format("$%,.2f", price.toDollar());
    }
}
