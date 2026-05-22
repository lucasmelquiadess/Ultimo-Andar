package br.com.ultimoandar.contracts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank(message = "Informe a senha temporária.")
        @Size(min = 10, message = "A senha temporária deve ter pelo menos 10 caracteres.")
        String newPassword
) {
}
