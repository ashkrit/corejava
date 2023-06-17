package org.refactoring;

import org.junit.jupiter.api.Test;
import org.refactoring.gson.GsonMessage;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatementGeneratorTest {

    @Test
    public void verify_customer_statement_from_legacy_code() {

        Invoices invoices = GsonMessage.fromClasspath("/invoices.json", Invoices.class);
        Plays plays = GsonMessage.fromClasspath("/plays.json", Plays.class);
        String statement = StatementGenerator.generate(invoices.orders.get(0), plays);

        String[] lines = statement.split("\n");

        assertAll(
                () -> assertEquals("Statement for BigCo", lines[0].trim()),
                () -> assertEquals("Hamlet: 650.0 55 seats", lines[1].trim()),
                () -> assertEquals("As You Like It: 580.0 35 seats", lines[2].trim()),
                () -> assertEquals("Othello: 500.0 40 seats", lines[3].trim()),
                () -> assertEquals("Amount owed is 1730.0", lines[4].trim()),
                () -> assertEquals("You earned 47.0 credits", lines[5].trim())
        );

    }


}
