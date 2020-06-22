package bitfiddle.apps;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import static bitfiddle.MoreInts.toInt;

public class HammingDistanceTest {

    @Test
    public void weight() {
        Assert.assertEquals(1,
                HammingDistance.weight(toInt("1")));
    }

    @Test
    public void multiBytesWeight() {
        Assert.assertEquals(2,
                HammingDistance.weight(toInt("10100")));
    }


    @Test
    public void distance() {
        Assert.assertEquals(2,
                HammingDistance.distance(toInt("10100"), toInt("00101")));
    }

}
