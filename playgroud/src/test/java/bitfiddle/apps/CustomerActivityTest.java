package bitfiddle.apps;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomerActivityTest {
    DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Test
    public void monthly_active_customer() {

        CustomerActivity activity = new CustomerActivity();
        activity.record(toDate("20200101"));
        activity.record(toDate("20200105"));
        activity.record(toDate("20200112"));
        activity.record(toDate("20200119"));
        activity.record(toDate("20200125"));
        activity.record(toDate("20200126"));

        assertEquals(6, activity.daysActive(Month.JANUARY));
        assertEquals(0, activity.daysActive(Month.FEBRUARY));

    }


    @Test
    public void daily_active_customer_check() {

        CustomerActivity activity = new CustomerActivity();
        activity.record(toDate("20200101"));
        activity.record(toDate("20200126"));
        activity.record(toDate("20200131"));
        activity.record(toDate("20200221"));
        activity.record(toDate("20200521"));

        activity.prettyPrint();
        assertEquals(true, activity.wasActive(toDate("20200126")));
        assertEquals(false, activity.wasActive(toDate("20200102")));

    }


    private LocalDate toDate(String text) {
        return LocalDate.parse(text, yyyyMMdd);
    }
}
