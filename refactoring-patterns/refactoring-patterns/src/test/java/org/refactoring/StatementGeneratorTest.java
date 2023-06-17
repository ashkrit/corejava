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
        StatementGenerator generator = new LegacyStatementGenerator();

        String statement = generator.generate(invoices.orders.get(0), plays);

        String[] lines = statement.split("\n");

        assertAll(
                () -> assertEquals("Statement for BigCo", lines[0].trim()),
                () -> assertEquals("Hamlet: 650.00 55 seats", lines[1].trim()),
                () -> assertEquals("As You Like It: 580.00 35 seats", lines[2].trim()),
                () -> assertEquals("Othello: 500.00 40 seats", lines[3].trim()),
                () -> assertEquals("Amount owed is 1730.00", lines[4].trim()),
                () -> assertEquals("You earned 47.00 credits", lines[5].trim())
        );

    }


}
