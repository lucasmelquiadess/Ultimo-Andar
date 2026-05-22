package br.com.ultimoandar.contracts.security;

import br.com.ultimoandar.contracts.repository.AppUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class MustChangePasswordFilter extends OncePerRequestFilter {

    private final AppUserRepository repository;

    public MustChangePasswordFilter(AppUserRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String path = request.getRequestURI();
        boolean allowed = HttpMethod.OPTIONS.matches(request.getMethod())
                || path.equals("/api/auth/login")
                || path.equals("/api/auth/me")
                || path.equals("/api/auth/change-password");

        if (!allowed && authentication != null && authentication.isAuthenticated()) {
            boolean mustChange = repository.findByUsernameIgnoreCase(authentication.getName())
                    .map(user -> user.isActive() && user.isMustChangePassword())
                    .orElse(false);
            if (mustChange && path.startsWith("/api/")) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Troque a senha antes de continuar.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
