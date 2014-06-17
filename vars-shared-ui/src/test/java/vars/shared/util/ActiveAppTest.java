package vars.shared.util;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;

/**
 * Created by brian on 6/17/14.
 */
public class ActiveAppTest {

    private static final String MSG = "Yattaa";
    Collection<Integer> ports = Lists.newArrayList(4004, 4008, 4012, 5000, 6013, 6999, 7019, 7059);

    @Test
    public void liveCycleTest() {
        ActiveAppBeacon beacon = new ActiveAppBeacon(ports, MSG);
        Assert.assertTrue(beacon.isAlive());
        beacon.kill();
        Assert.assertFalse(beacon.isAlive());
    }

    @Test
    public void pingTest() {
        ActiveAppBeacon beacon = new ActiveAppBeacon(ports, MSG);
        boolean isBeacon = ActiveAppPinger.pingAll(ports, MSG);
        Assert.assertTrue("Whoops, beacon wasn't alive", beacon.isAlive());
        Assert.assertTrue("Expected a beacon to respond with " + MSG + " but none did", isBeacon);
        beacon.kill();
    }

}
