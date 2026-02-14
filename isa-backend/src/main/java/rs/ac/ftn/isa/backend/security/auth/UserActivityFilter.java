package rs.ac.ftn.isa.backend.security.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import rs.ac.ftn.isa.backend.service.UserActivityService;
import rs.ac.ftn.isa.backend.util.TokenUtils;

import java.io.IOException;

@Component
public class UserActivityFilter extends OncePerRequestFilter {

    private final UserActivityService userActivityService;
    private final TokenUtils tokenUtils;

    public UserActivityFilter(UserActivityService userActivityService, TokenUtils tokenUtils) {
        this.userActivityService = userActivityService;
        this.tokenUtils = tokenUtils;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // Ne brojimo actuator i auth rute
        if (path.startsWith("/actuator")) return true;
        if (path.startsWith("/auth")) return true;

        // Preflight
        if (HttpMethod.OPTIONS.matches(request.getMethod())) return true;

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth instanceof TokenBasedAuthentication tba) {
                String token = (String) tba.getCredentials(); // JWT string
                Long userId = tokenUtils.getUserIdFromToken(token);

                if (userId != null) {
                    userActivityService.touch(userId);
                }
            }
        } catch (Exception ignored) {
            // namerno: metrika ne sme da obori request
        }

        filterChain.doFilter(request, response);
    }
}