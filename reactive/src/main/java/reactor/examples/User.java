package reactor.examples;

public class User {

    private final String firstName;

    public User(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    @Override
    public String toString() {
        return String.format("User[%s]", firstName);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof User) {
            var that = (User) other;
            return that.getFirstName().equals(firstName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 31 * firstName.hashCode();
    }
}
