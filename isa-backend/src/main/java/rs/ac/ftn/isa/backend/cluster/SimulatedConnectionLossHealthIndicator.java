package rs.ac.ftn.isa.backend.cluster;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Kada je simuliran gubitak konekcije, ovaj indicator vraća DOWN.
 * Load balancer tada šalje samo na drugu repliku.
 */
@Component
public class SimulatedConnectionLossHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        if (SimulatedConnectionLossHolder.isSimulatedDown()) {
            return Health.down().withDetail("simulated", "Gubitak konekcije ka bazi (simulacija)").build();
        }
        return Health.up().build();
    }
}
