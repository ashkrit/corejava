package org.refactoring;


import org.refactoring.Invoices.Order.Performance;

public class PlayTypeCalculator {

    public static final int DEFAULT_AUDIENCE_THRESHOLD = 30;
    public static final int COMEDY_AUDIENCE_THRESHOLD = 20;

    public static double comedyCharge(Performance performance) {
        double thisAmount = 30000;
        int audience = performance.audience;

        if (audience > COMEDY_AUDIENCE_THRESHOLD) {
            thisAmount += 10000 + 500 * (audience - COMEDY_AUDIENCE_THRESHOLD);
        }
        thisAmount += 300 * audience;
        return thisAmount;
    }

    public static double tradeCharge(Performance performance) {
        double thisAmount = 40000;
        int audience = performance.audience;

        if (audience > DEFAULT_AUDIENCE_THRESHOLD) {
            thisAmount += 1000 * (audience - DEFAULT_AUDIENCE_THRESHOLD);
        }
        return thisAmount;
    }
}
