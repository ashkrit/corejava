package org.refactoring;

import org.refactoring.Invoices.Order.Performance;
import org.refactoring.Plays.Play;

public class RefactoredStatementGenerator implements StatementGenerator {

    public static final String COMEDY = "comedy";
    public static final String TRAGEDY = "tragedy";

    @Override
    public String generate(Invoices.Order order, Plays plays) {

        double totalAmount = 0;
        double volumeCredits = 0;

        StringBuilder result = new StringBuilder();
        result.append(String.format("Statement for %s \n", order.customer));
        for (Performance performance : order.performances) {


            Play play = findPlay(plays, performance.playID);
            double thisAmount = calculateAmount(performance, play);
            volumeCredits += calculateVolumeCredit(performance, play);

            // print line for this order
            result
                    .append(String.format("%s: %.2f %s seats \n", play.name, thisAmount, performance.audience));

            totalAmount += thisAmount;

        }

        result
                .append(String.format("Amount owed is %.2f \n", totalAmount))
                .append(String.format("You earned %.2f credits \n", volumeCredits));

        return result.toString();
    }

    private static double calculateVolumeCredit(Performance performance, Play performancePlay) {
        // add volume calculateAmount
        double volumeCredit = Math.max(performance.audience - 30, 0);
        // add extra credit for every ten comedy attendees
        if (COMEDY.equals(performancePlay.type)) {
            volumeCredit += Math.floor(performance.audience / 5);
        }
        return volumeCredit;
    }

    private static double calculateAmount(Performance performance, Play performancePlay) {
        double thisAmount;
        switch (performancePlay.type) {

            case TRAGEDY: {
                thisAmount = 40000;
                if (performance.audience > 30) {
                    thisAmount += 1000 * (performance.audience - 30);
                }
                break;
            }
            case COMEDY: {
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
        return thisAmount / 100;
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
