package vars.annotation;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Brian Schlining
 * @since 2015-10-20T15:20:00
 */
public class PhysicalDataValueEqTest {



    @Test
    public void identityTest() {
        IValueEq<PhysicalData> eq = new PhysicalDataValueEq();
        PhysicalData a = new ImmutablePhysicalData(null, 10F, 10F, 10D, 10F, null, 10D, 10F, 10F, 10F);
        assertTrue(eq.equal(a, a));
    }

    @Test
    public void nullValueTest() {
        IValueEq<PhysicalData> eq = new PhysicalDataValueEq();
        PhysicalData a = new ImmutablePhysicalData(null, 10F, 10F, 10D, 10F, null, 10D, 10F, 10F, 10F);
        PhysicalData b = new ImmutablePhysicalData(null, 10F, 10F, 10D, 10F, null, 10D, 10F, 10F, null);
        assertFalse(eq.equal(a, b));
    }

    @Test
    public void toleranceTest1() {
        IValueEq<PhysicalData> eq = new PhysicalDataValueEq();
        PhysicalData a = new ImmutablePhysicalData(null, 10F, 10F, 10D, 10F, null, 10D, 10F, 10F, 10F);
        PhysicalData b = new ImmutablePhysicalData(null, 10F, 10F, 11D, 10F, null, 10D, 10F, 10F, 10F);
        assertFalse(eq.equal(a, b));
    }

    @Test
    public void toleranceTest2() {
        IValueEq<PhysicalData> eq = new PhysicalDataValueEq(0.01);
        PhysicalData a = new ImmutablePhysicalData(null, 10F, 10F, 10D, 10F, null, 10D, 10F, 10F, 10F);
        PhysicalData b = new ImmutablePhysicalData(null, 10F, 10F, 10.01, 10F, null, 10D, 10F, 10F, 10F);
        assertTrue(eq.equal(a, b));
    }

    @Test
    public void toleranceTest3() {
        IValueEq<PhysicalData> eq = new PhysicalDataValueEq(0.001);
        PhysicalData a = new ImmutablePhysicalData(null, 10F, 10F, 10D, 10F, null, 10D, 10F, 10F, 10F);
        PhysicalData b = new ImmutablePhysicalData(null, 10F, 10F, 10.002, 10F, null, 10D, 10F, 10F, 10F);
        assertFalse(eq.equal(a, b));
    }

}
