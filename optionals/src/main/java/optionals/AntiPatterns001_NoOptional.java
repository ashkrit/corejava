package optionals;

public class AntiPatterns001_NoOptional {

    public static void main(String[] args) {

        Person p = new Person("James", "Bond", "65-6666-700", null);

        //Not using optional
        if (p.email != null) {
            System.out.println("Sending email to " + p.email);
        }

        if (p.phone != null) {
            System.out.println("Calling " + p.phone);
        }

        //Use Optional
        p.getEmail().ifPresent(email -> System.out.println("Sending email to " + email));
        p.getPhone().ifPresent(phone -> System.out.println("Calling " + phone));

    }
}
