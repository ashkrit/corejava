package optionals;

import java.util.Optional;

import static java.util.Optional.ofNullable;

public class AntiPatterns004_NestedProperty {

    public static void main(String[] args) {

        Person p = new Person("James", "Bond", null, null);

        //Nested Property
        if (p.getHome() != null) {
            System.out.println("Sending Postal mail " + p.getHome().address);
        }


        if (p.getHome() != null && p.getHome().getInsurance() != null) {
            System.out.println("Sending Notification to insurance " + p.getHome().getInsurance().getAgency());
        }

    }

    private static Optional<String> email(Person p) {
        return
                ofNullable(p.email == null ? "NA" : p.email);
    }

    private static Optional<String> contactNumber(Person p) {
        return ofNullable(p.phone == null ? "NA" : p.phone);
    }


}
