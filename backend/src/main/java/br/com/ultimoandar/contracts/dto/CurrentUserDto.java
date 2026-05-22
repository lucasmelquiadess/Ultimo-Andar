package br.com.ultimoandar.contracts.dto;

import br.com.ultimoandar.contracts.entity.enums.UserRole;

public record CurrentUserDto(
        String username,
        String displayName,
        UserRole role,
        boolean mustChangePassword
) {
}
