package optionals;

import java.util.Optional;

public class Person {
    final String email;
    final String firstName;
    final String lastName;
    final String phone;
    final Home home;
    final Office office;

    public Person(String firstName, String lastName, String phone, String email) {
        this(firstName, lastName, phone, email, null, null);
    }

    public Person(String firstName, String lastName, String phone, String email, Home home, Office office) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.home = home;
        this.office = office;
    }

    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }

    public Optional<String> getPhone() {
        return Optional.ofNullable(phone);
    }

    public Optional<Home> getHome() {
        return Optional.ofNullable(home);
    }

    public static class Home {
        final String address;
        final Insurance insurance;

        public Home(String address, Insurance insurance) {
            this.address = address;
            this.insurance = insurance;
        }

        public Optional<Insurance> getInsurance() {
            return Optional.ofNullable(insurance);
        }

        public String getAddress() {
            return address;
        }
    }

    public static class Office {
        final String address;

        public Office(String address) {
            this.address = address;
        }

        public String getAddress() {
            return address;
        }
    }

    public static class Insurance {
        final String agency;
        final double installment;

        public Insurance(String agency, double installment) {
            this.agency = agency;
            this.installment = installment;
        }

        public double getInstallment() {
            return installment;
        }

        public String getAgency() {
            return agency;
        }
    }
}
