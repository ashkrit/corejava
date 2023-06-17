package org.refactoring;

import org.refactoring.Invoices.Order.Performance;
import org.refactoring.Plays.Play;

public class RefactoredStatementGenerator implements StatementGenerator {

    public static final int DEFAULT_AUDIENCE_THRESHHOLD = 30;
    public static final int COMEDY_AUDIENCE_THRESHHOLD = 20;

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
        double volumeCredit = Math.max(performance.audience - DEFAULT_AUDIENCE_THRESHHOLD, 0);
        // add extra credit for every ten comedy attendees
        if (PlayType.COMEDY.typeName.equals(performancePlay.type)) {
            volumeCredit += Math.floor(performance.audience / 5);
        }
        return volumeCredit;
    }

    private static double calculateAmount(Performance performance, Play play) {
        double thisAmount;
        switch (play.playType()) {

            case TRAGEDY: {
                thisAmount = tradegyCharge(performance);
                break;
            }
            case COMEDY: {
                thisAmount = commedyCharge(performance);
                break;
            }
            default:
                throw new IllegalArgumentException(String.format("Unsupported Play type %s", play.type));

        }
        return thisAmount / 100;
    }

    private static double commedyCharge(Performance performance) {
        double thisAmount;
        thisAmount = 30000;
        if (performance.audience > COMEDY_AUDIENCE_THRESHHOLD) {
            thisAmount += 10000 + 500 * (performance.audience - COMEDY_AUDIENCE_THRESHHOLD);
        }
        thisAmount += 300 * performance.audience;
        return thisAmount;
    }

    private static double tradegyCharge(Performance performance) {
        double thisAmount;
        thisAmount = 40000;
        if (performance.audience > DEFAULT_AUDIENCE_THRESHHOLD) {
            thisAmount += 1000 * (performance.audience - DEFAULT_AUDIENCE_THRESHHOLD);
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
