package optionals.antipattern;

import java.util.Optional;

public class AntiPatterns003_AlwaysHappyOptional {

    public static void main(String[] args) {

        Person p = new Person("James", "Bond", null, null);

        //Always Happy
        Optional<String> phone = contactNumber(p);
        Optional<String> email = email(p);

        //Don't do this
        System.out.println("Calling Phone " + phone.get());
        System.out.println("Sending Email " + email.get());

        //Use ifPresent to avoid runtime error
        phone.ifPresent(contact -> System.out.println("Sending email to " + contact));
        email.ifPresent(contact -> System.out.println("Calling " + contact));
    }

    private static Optional<String> email(Person p) {
        return
                Optional.ofNullable(p.email == null ? "NA" : p.email);
    }

    private static Optional<String> contactNumber(Person p) {
        return Optional.ofNullable(p.phone == null ? "NA" : p.phone);
    }
}
