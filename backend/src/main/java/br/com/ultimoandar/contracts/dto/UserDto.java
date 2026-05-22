package br.com.ultimoandar.contracts.dto;

import br.com.ultimoandar.contracts.entity.enums.UserRole;
import java.time.Instant;
import java.util.UUID;

public record UserDto(
        UUID id,
        String username,
        String displayName,
        UserRole role,
        boolean active,
        boolean mustChangePassword,
        Instant createdAt,
        Instant updatedAt
) {
}
