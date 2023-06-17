package org.refactoring;

import org.refactoring.Invoices.Order;

public interface StatementGenerator {
    String generate(Order order, Plays plays);
}
