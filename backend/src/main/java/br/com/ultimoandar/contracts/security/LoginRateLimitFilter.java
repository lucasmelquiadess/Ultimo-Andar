package br.com.ultimoandar.contracts.security;

import br.com.ultimoandar.contracts.config.SecurityProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private static final long WINDOW_SECONDS = 60;

    private final SecurityProperties properties;
    private final Map<String, Counter> counters = new ConcurrentHashMap<>();

    public LoginRateLimitFilter(SecurityProperties properties) {
        this.properties = properties;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return !properties.enabled()
                || !HttpMethod.POST.matches(request.getMethod())
                || !"/api/auth/login".equals(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String key = clientIp(request);
        long now = Instant.now().getEpochSecond();
        Counter counter = counters.compute(key, (ignored, current) -> {
            if (current == null || now - current.windowStart() >= WINDOW_SECONDS) {
                return new Counter(now, 1);
            }
            return new Counter(current.windowStart(), current.count() + 1);
        });

        if (counter.count() > properties.loginRateLimitPerMinute()) {
            response.sendError(429, "Muitas tentativas de login. Aguarde um minuto e tente novamente.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private record Counter(long windowStart, int count) {
    }
}
