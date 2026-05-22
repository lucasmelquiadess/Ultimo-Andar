package br.com.ultimoandar.contracts.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(
        boolean enabled,
        String username,
        String password,
        String jwtSecret,
        long tokenMinutes,
        int maxLoginAttempts,
        long lockMinutes,
        int loginRateLimitPerMinute
) {
}
