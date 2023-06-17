package org.refactoring;

import org.refactoring.Invoices.Order.Performance;
import org.refactoring.Plays.Play;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class RefactoredStatementGenerator implements StatementGenerator {

    private final Map<PlayType, Function<Performance, Double>> planTypeCalculator = new HashMap<>();

    public RefactoredStatementGenerator() {
        planTypeCalculator.put(PlayType.COMEDY, PlayTypeCalculator::comedyCharge);
        planTypeCalculator.put(PlayType.TRAGEDY, PlayTypeCalculator::tradeCharge);
    }

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

    private static double calculateVolumeCredit(Performance performance, Play play) {
        // add volume calculateAmount
        double volumeCredit = Math.max(performance.audience - PlayTypeCalculator.DEFAULT_AUDIENCE_THRESHOLD, 0);
        // add extra credit for every ten comedy attendees
        volumeCredit += addExtraVolumeCredit(performance, play);
        return volumeCredit;
    }

    private static double addExtraVolumeCredit(Performance performance, Play play) {
        if (isComedy(play)) {
            int reducedAudience = performance.audience / 5;
            return Math.floor(reducedAudience);
        }
        return 0;
    }

    private static boolean isComedy(Play performancePlay) {
        return PlayType.COMEDY.typeName.equals(performancePlay.type);
    }

    private double calculateAmount(Performance performance, Play play) {

        Function<Performance, Double> fn = planTypeCalculator.get(play.playType());
        if (fn == null) {
            throw new IllegalArgumentException(String.format("Unsupported Play type %s", play.type));
        }
        return fn
                .andThen(v -> v / 100)
                .apply(performance);


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
