package org.refactoring;

import org.refactoring.Invoices.Order.Performance;
import org.refactoring.Plays.Play;

public class StatementGenerator {

    public static String generate(Invoices.Order order, Plays plays) {

        double totalAmount = 0;
        double volumeCredits = 0;

        String result = String.format("Statement for %s \n", order.customer);
        for (Performance performance : order.performances) {

            double thisAmount = 0;
            String playId = performance.playID;
            Play performancePlay = null;
            for (Play play : plays.plays) {
                if (play.playID.equals(playId)) {
                    performancePlay = play;
                }
            }


            switch (performancePlay.type) {

                case "tragedy": {
                    thisAmount = 40000;
                    if (performance.audience > 30) {
                        thisAmount += 1000 * (performance.audience - 30);
                    }
                    break;
                }
                case "comedy": {
                    thisAmount = 30000;
                    if (performance.audience > 20) {
                        thisAmount += 10000 + 500 * (performance.audience - 20);
                    }
                    thisAmount += 300 * performance.audience;
                    break;
                }
                default:
                    throw new IllegalArgumentException(String.format("Unsupported Play type %s", performancePlay.type));

            }

            // add volume credits
            volumeCredits += Math.max(performance.audience - 30, 0);
            // add extra credit for every ten comedy attendees
            if ("comedy".equals(performancePlay.type)) {
                volumeCredits += Math.floor(performance.audience / 5);
            }

            // print line for this order
            result += String.format("%s: %s %s seats \n", performancePlay.name, thisAmount / 100, performance.audience);
            totalAmount += thisAmount;

        }

        result += String.format("Amount owed is %s \n", totalAmount / 100);
        result += String.format("You earned %s credits \n", volumeCredits);

        return result;
    }

}
