package optionals;

public class AntiPatterns001_NoOptional {

    public static void main(String[] args) {

        Person p = new Person("James", "Bond", null, null);

        //Not using optional
        if (p.email != null) {
            System.out.println("Sending email to " + p.email);
        }

        if (p.phone != null) {
            System.out.println("Calling " + p.phone);
        }

    }
}
