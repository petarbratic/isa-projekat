package rs.ac.ftn.isa.backend.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;
import rs.ac.ftn.isa.backend.repository.UserActivityRepository;

@Component
public class ActiveUsersMetrics {

    public ActiveUsersMetrics(UserActivityRepository repository, MeterRegistry registry) {
        Gauge.builder("active_users_24h", repository,
                        repo -> repo.countUsersActiveInLast24Hours())
                .register(registry);
    }
}