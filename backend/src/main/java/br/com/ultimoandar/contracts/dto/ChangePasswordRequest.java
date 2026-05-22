package br.com.ultimoandar.contracts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank(message = "Informe a senha atual.")
        String currentPassword,
        @NotBlank(message = "Informe a nova senha.")
        @Size(min = 10, message = "A nova senha deve ter pelo menos 10 caracteres.")
        String newPassword
) {
}
