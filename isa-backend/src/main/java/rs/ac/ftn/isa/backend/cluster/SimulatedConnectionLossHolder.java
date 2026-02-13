package rs.ac.ftn.isa.backend.cluster;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Drži flag za simulaciju gubitka konekcije (parcijalni gubitak – jedna replika).
 * Kada je true, /actuator/health vraća DOWN, pa load balancer ne šalje na tu repliku.
 */
public class SimulatedConnectionLossHolder {
    private static final AtomicBoolean simulatedDown = new AtomicBoolean(false);

    public static void setSimulatedDown(boolean down) {
        simulatedDown.set(down);
    }

    public static boolean isSimulatedDown() {
        return simulatedDown.get();
    }
}
