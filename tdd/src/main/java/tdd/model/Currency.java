package tdd.model;

public class Currency {
    private final String currency;

    public Currency(String currency) {
        this.currency = currency;
    }

    public String name() {
        return currency;
    }
}
