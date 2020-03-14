package optionals.antipattern;

import java.util.Optional;

public class AntiPatterns002_UseOptionalInWrongWay {

    public static void main(String[] args) {

        Person p = new Person("James", "Bond", null, null);

        //Using in wrong way
        Optional<String> phone = contactNumber(p);
        Optional<String> email = email(p);

        if (phone.isPresent()) {
            System.out.println("Calling Phone " + phone.get());
        }
        if (email.isPresent()) {
            System.out.println("Sending Email " + email.get());
        }

        //Use IfPresent & other cool things
        phone
                .filter(number -> hasOptIn(number))
                .ifPresent(number -> System.out.println("Calling Phone " + number));

        email
                .filter(m -> hasOptIn(m))
                .ifPresent(m -> System.out.println("Sending Email " + m));
    }

    private static boolean hasOptIn(String p) {
        return true;
    }

    private static Optional<String> email(Person p) {
        return Optional.ofNullable(p.email);
    }

    private static Optional<String> contactNumber(Person p) {
        return Optional.ofNullable(p.phone);
    }


}
