package optionals;

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

    }

    private static Optional<String> email(Person p) {
        return Optional.ofNullable(p.email);
    }

    private static Optional<String> contactNumber(Person p) {
        return Optional.ofNullable(p.phone);
    }


}
