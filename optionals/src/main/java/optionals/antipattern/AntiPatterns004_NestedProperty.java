package optionals.antipattern;

import optionals.antipattern.Person.Home;

public class AntiPatterns004_NestedProperty {

    public static void main(String[] args) {

        Person p = new Person("James", "Bond", null, null, new Home("add1",
                new Person.Insurance("AXA", 100)), null);

        //Nested Property
        if (p.home != null) {
            System.out.println("Sending Postal mail " + p.home.address);
        }

        if (p.home != null && p.home.insurance != null) {
            System.out.println("Sending Notification to insurance " + p.home.insurance.agency);
        }

        p.getHome().ifPresent(a -> System.out.println("Sending Postal mail " + a.address));
        p.getHome()
                .flatMap(Home::getInsurance)
                .ifPresent(a -> System.out.println("Sending Notification to insurance " + a.agency));

    }

}
