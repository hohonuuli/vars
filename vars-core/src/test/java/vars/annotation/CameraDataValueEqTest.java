package vars.annotation;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * @author Brian Schlining
 * @since 2015-10-21T09:41:00
 */
public class CameraDataValueEqTest {


    @Test
    public  void identityTest() {
        IValueEq<CameraData> eq = new CameraDataValueEq();
        CameraData a = new ImmutableCameraData(null, "a", 1D, 2, 3F, "aa", 4, null, "aaa", 5F, 6F, 7F, 8F, "aaaa", 9F,
                "aaaaa", 10F, 11F, "aaaaaa", 12);
        assertTrue(eq.equal(a, a));
    }

    @Test
    public  void nullValueTest() {
        IValueEq<CameraData> eq = new CameraDataValueEq();
        CameraData a = new ImmutableCameraData(null, "a", 1D, 2, 3F, "aa", 4, null, "aaa", 5F, 6F, 7F, 8F, "aaaa", 9F,
                "aaaaa", 10F, 11F, "aaaaaa", 12);
        CameraData b = new ImmutableCameraData(null, "a", null, 2, 3F, "aa", 4, null, "aaa", 5F, 6F, 7F, 8F, "aaaa", 9F,
                "aaaaa", 10F, 11F, "aaaaaa", 12);
        assertFalse(eq.equal(a, b));
    }

    @Test
    public  void toleranceTest1() {
        IValueEq<CameraData> eq = new CameraDataValueEq();
        CameraData a = new ImmutableCameraData(null, "a", 1D, 2, 3F, "aa", 4, null, "aaa", 5F, 6F, 7F, 8F, "aaaa", 9F,
                "aaaaa", 10F, 11F, "aaaaaa", 12);
        CameraData b = new ImmutableCameraData(null, "a", 1.01, 2, 3F, "aa", 4, null, "aaa", 5F, 6F, 7F, 8F, "aaaa", 9F,
                "aaaaa", 10F, 11F, "aaaaaa", 12);
        assertFalse(eq.equal(a, b));
    }

    @Test
    public  void toleranceTest2() {
        IValueEq<CameraData> eq = new CameraDataValueEq(0.02);
        CameraData a = new ImmutableCameraData(null, "a", 1D, 2, 3F, "aa", 4, null, "aaa", 5F, 6F, 7F, 8F, "aaaa", 9F,
                "aaaaa", 10F, 11F, "aaaaaa", 12);
        CameraData b = new ImmutableCameraData(null, "a", 1.01, 2, 3F, "aa", 4, null, "aaa", 5F, 6F, 7F, 8F, "aaaa", 9F,
                "aaaaa", 10F, 11F, "aaaaaa", 12);
        assertTrue(eq.equal(a, b));
    }
}
