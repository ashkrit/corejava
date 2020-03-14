package optionals;

public class AntiPatterns005_PriorityBasedDefaultProperty {

    public static void main(String[] args) {

        Person p = new Person("James", "Bond", null, null, null, new Person.Office("Downtown"));

        //Address has priority , first home and then Office

        if (p.home != null) {
            System.out.println("Contacted at home address " + p.home.address);
            return; // Magical return for early exit
        }

        if (p.office != null) {
            System.out.println("Contacted at office address " + p.office.address);
            return; // Magical return for early exit
        }
    }
}
