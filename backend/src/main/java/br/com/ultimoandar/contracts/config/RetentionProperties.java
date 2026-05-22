package br.com.ultimoandar.contracts.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.retention")
public record RetentionProperties(
        int auditDays
) {
}
