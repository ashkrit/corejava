package org.refactoring;

import java.util.List;

public class Invoices {

    public final List<Order> orders;

    public Invoices(List<Order> orders) {
        this.orders = orders;
    }

    public static class Order {
        public final String customer;
        public final List<Performance> performances;

        Order(String customer, List<Performance> performances) {
            this.customer = customer;
            this.performances = performances;
        }


        public static class Performance {
            public final String playID;
            public final int audience;

            Performance(String playID, int audience) {
                this.playID = playID;
                this.audience = audience;
            }
        }
    }
}
