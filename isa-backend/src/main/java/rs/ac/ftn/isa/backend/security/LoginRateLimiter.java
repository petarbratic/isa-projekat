package rs.ac.ftn.isa.backend.security;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimiter {

    private final RateLimiterConfig baseConfig;
    private final ConcurrentHashMap<String, RateLimiter> perIp = new ConcurrentHashMap<>();

    public LoginRateLimiter(RateLimiterRegistry registry) {
        this.baseConfig = registry.rateLimiter("login").getRateLimiterConfig();
    }

    public boolean allow(String ip) {
        RateLimiter rl = perIp.computeIfAbsent(ip, k -> RateLimiter.of("login-" + k, baseConfig));
        return rl.acquirePermission();
    }
}
