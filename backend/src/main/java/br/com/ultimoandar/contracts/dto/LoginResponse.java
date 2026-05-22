package br.com.ultimoandar.contracts.dto;

public record LoginResponse(
        String token,
        long expiresAt,
        CurrentUserDto user
) {
}
