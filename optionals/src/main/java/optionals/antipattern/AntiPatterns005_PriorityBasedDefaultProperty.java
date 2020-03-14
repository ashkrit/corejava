package optionals.antipattern;

import optionals.antipattern.Person.Home;
import optionals.antipattern.Person.Office;

import java.util.Optional;
import java.util.stream.Stream;

public class AntiPatterns005_PriorityBasedDefaultProperty {

    public static void main(String[] args) {

        Person person = new Person("James", "Bond",
                null, null, new Home("at beach", null),
                new Office("Downtown"));

        //Address has priority , first home and then Office

        contactCustomer(person);

        Optional<String> address = Stream
                .of(person.getHome().map(Home::getAddress), person.getOffice().map(Office::getAddress))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

        address
                .ifPresent(add -> System.out.println("Contacting at address " + add));
    }

    private static void contactCustomer(Person p) {
        if (p.home != null) {
            System.out.println("Contacted at home address " + p.home.address);
            return;
        }
        if (p.office != null) {
            System.out.println("Contacted at office address " + p.office.address);
            return;
        }
    }
}
