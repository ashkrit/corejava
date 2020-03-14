package optionals.antipattern;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AntiPatterns001_NoOptionalForCollection {

    public static void main(String[] args) {

        List<Person> persons = searchPersonById("100");

        if (persons.isEmpty()) {
            System.out.println("No result");
        } else {
            System.out.println("Person" + persons.get(0));
        }

        //Use Optional
        Optional<Person> person = persons.stream().findFirst();
        person.ifPresent(System.out::println);

    }

    private static List<Person> searchPersonById(String id) {
        return Arrays.asList(new Person("James", "Bond", "65-6666-700", null));
    }
}
