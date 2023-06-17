package org.refactoring;

import org.refactoring.gson.GsonMessage;

public class App {

    public static void main(String[] args) {
        Invoices invoices = GsonMessage.fromClasspath("/invoices.json", Invoices.class);
        Plays plays = GsonMessage.fromClasspath("/plays.json", Plays.class);

        StatementGenerator generator = new LegacyStatementGenerator();
        String statement = generator.generate(invoices.orders.get(0), plays);

        System.out.println(statement);
    }
}
