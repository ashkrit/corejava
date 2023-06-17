package org.refactoring;

import org.refactoring.gson.GsonMessage;

public class App {

    public static void main(String[] args) {
        Invoices invoices = GsonMessage.fromClasspath("/invoices.json", Invoices.class);
        Plays plays = GsonMessage.fromClasspath("/plays.json", Plays.class);

        String statement = StatementGenerator.generate(invoices.orders.get(0), plays);

        System.out.println(statement);
    }
}
