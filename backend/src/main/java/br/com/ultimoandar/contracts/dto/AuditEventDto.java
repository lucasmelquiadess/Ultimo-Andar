package br.com.ultimoandar.contracts.dto;

import java.time.Instant;
import java.util.UUID;

public record AuditEventDto(
        UUID id,
        Instant createdAt,
        String actor,
        String action,
        String resourceType,
        String resourceId,
        String details,
        String ipAddress
) {
}
