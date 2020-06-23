package bitfiddle.apps;

import bitfiddle.Bits;

import java.time.LocalDate;
import java.time.Month;
import java.util.stream.IntStream;

/*
  E-commerce company wants to keep tracks of days customer was active in given year.
  This example shows usage of bit operator ( << , | , &) & bit count

  This impl can store 365 days activity using just 48 bytes ( 4 * 12). 1 Million customer can be stored in just 45 MB.

  Other options to store such information
  - Maintain sparse Set<Day>. This will be expensive as we have to pay cost of Day object for every customer and Set also brings some overhead in terms of extra allocation it is to do to provide O(1) lookup time.
  - Maintain boolean array of 365 capacity. This will takes 365 bytes(i.e 10X more!)

  Another benefit of keeping value packed in single Int that it enables bitwise operation to find records match.

 */

public class CustomerActivity {

    private final int[] months = new int[12];

    public void record(LocalDate day) {
        int monthOffSet = day.getMonthValue() - 1;
        int monthValue = months[monthOffSet];
        months[monthOffSet] = setDayBit(day, monthValue);
    }

    private int setDayBit(LocalDate day, int monthValue) {
        return monthValue | createDayBit(day);
    }

    private int createDayBit(LocalDate day) {
        return 1 << (day.getDayOfMonth() - 1);
    }

    public int daysActive(Month month) {
        int monthValue = months[month.ordinal()];
        return Bits.countBits(monthValue);
    }

    public boolean wasActive(LocalDate day) {
        int monthOffSet = day.getMonthValue() - 1;
        int monthValue = months[monthOffSet];
        int dayBit = createDayBit(day);
        return Bits.countBits(monthValue & dayBit) > 0;
    }


    public void prettyPrint() {
        IntStream
                .range(0, months.length)
                .filter(index -> months[index] > 0)
                .forEach(index -> printMonthUsage(index));
    }

    private void printMonthUsage(int index) {
        System.out.println(String.format("%s\n  (%s) -> %s", Month.of(index + 1),
                Integer.toBinaryString(months[index]),
                months[index]));
    }
}
