package org.refactoring;

import org.refactoring.Invoices.Order.Performance;
import org.refactoring.Plays.Play;

public class RefactoredStatementGenerator implements StatementGenerator {

    @Override
    public String generate(Invoices.Order order, Plays plays) {

        double totalAmount = 0;
        double volumeCredits = 0;

        String result = String.format("Statement for %s \n", order.customer);
        for (Performance performance : order.performances) {


            Play play = findPlay(plays, performance.playID);
            double thisAmount = caluclateAmount(performance, play);
            volumeCredits = calculateVolumnCredit(volumeCredits, performance, play);

            // print line for this order
            result += String.format("%s: %.2f %s seats \n", play.name, thisAmount / 100, performance.audience);
            totalAmount += thisAmount;

        }

        result += String.format("Amount owed is %.2f \n", totalAmount / 100);
        result += String.format("You earned %.2f credits \n", volumeCredits);

        return result;
    }

    private static double calculateVolumnCredit(double volumeCredits, Performance performance, Play performancePlay) {
        // add volume credits
        volumeCredits += Math.max(performance.audience - 30, 0);
        // add extra credit for every ten comedy attendees
        if ("comedy".equals(performancePlay.type)) {
            volumeCredits += Math.floor(performance.audience / 5);
        }
        return volumeCredits;
    }

    private static double caluclateAmount(Performance performance, Play performancePlay) {
        double thisAmount;
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
        return thisAmount;
    }

    private static Play findPlay(Plays plays, String playId) {
        return plays
                .plays
                .stream()
                .filter(p -> p.playID.equals(playId))
                .findFirst()
                .get();
    }

}
