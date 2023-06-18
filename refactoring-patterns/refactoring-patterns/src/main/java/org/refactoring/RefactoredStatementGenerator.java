package org.refactoring;

import org.refactoring.Invoices.Order.Performance;
import org.refactoring.Plays.Play;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RefactoredStatementGenerator implements StatementGenerator {

    private final Map<PlayType, Function<Performance, Double>> planTypeCalculator = new HashMap<>();

    public RefactoredStatementGenerator() {
        planTypeCalculator.put(PlayType.COMEDY, PlayTypeCalculator::comedyCharge);
        planTypeCalculator.put(PlayType.TRAGEDY, PlayTypeCalculator::tradeCharge);
    }

    @Override
    public String generate(Invoices.Order order, Plays plays) {


        String lineBreak = "\n";
        StringBuilder result = new StringBuilder();
        result
                .append(String.format("Statement for %s", order.customer))
                .append(lineBreak);


        String billText = order.performances
                .stream()
                .map(performance -> new PerformanceCharges(performance, findPlay(plays, performance.playID), 0.0d))
                .map(performanceCharges -> new PerformanceCharges(performanceCharges.performance, performanceCharges.play, calculateAmount(performanceCharges.performance, performanceCharges.play)))
                .map(performanceCharges -> String.format("%s: %.2f %s seats ", performanceCharges.play.name, performanceCharges.amount, performanceCharges.performance.audience))
                .collect(Collectors.joining(lineBreak));


        double totalAmount = order.performances
                .stream()
                .map(performance -> calculateAmount(performance, findPlay(plays, performance.playID)))
                .mapToDouble(Double::doubleValue)
                .sum();

        double creditAmount = order.performances
                .stream()
                .map(performance -> calculateVolumeCredit(performance, findPlay(plays, performance.playID)))
                .mapToDouble(Double::doubleValue)
                .sum();


        result
                .append(billText)
                .append(lineBreak)
                .append(String.format("Amount owed is %.2f", totalAmount))
                .append(lineBreak)
                .append(String.format("You earned %.2f credits", creditAmount));

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

    static class PerformanceCharges {
        public final Performance performance;
        public final Play play;

        public final double amount;

        PerformanceCharges(Performance performance, Play play, double amount) {
            this.performance = performance;
            this.play = play;
            this.amount = amount;
        }
    }

}
