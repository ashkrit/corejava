package optionals;

import java.util.Optional;

import static java.util.Optional.ofNullable;

public class AntiPatterns004_NestedProperty {

    public static void main(String[] args) {

        Person p = new Person("James", "Bond", null, null, new Person.Home("add1",
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
                .flatMap(Person.Home::getInsurance)
                .ifPresent(a -> System.out.println("Sending Notification to insurance " + a.agency));

    }

    private static Optional<String> email(Person p) {
        return
                ofNullable(p.email == null ? "NA" : p.email);
    }

    private static Optional<String> contactNumber(Person p) {
        return ofNullable(p.phone == null ? "NA" : p.phone);
    }


}
