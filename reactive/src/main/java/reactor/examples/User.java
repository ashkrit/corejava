package reactor.examples;

public class User {

    private final String firstName;
    private final String middleName;
    private final String lastName;

    public User(String firstName, String middleName, String lastName) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }
}
