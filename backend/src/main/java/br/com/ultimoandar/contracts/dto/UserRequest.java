package br.com.ultimoandar.contracts.dto;

import br.com.ultimoandar.contracts.entity.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank(message = "Informe o usuário.")
        @Size(max = 80, message = "O usuário deve ter no máximo 80 caracteres.")
        String username,
        @NotBlank(message = "Informe o nome de exibição.")
        @Size(max = 160, message = "O nome de exibição deve ter no máximo 160 caracteres.")
        String displayName,
        @NotBlank(message = "Informe a senha.")
        @Size(min = 8, message = "A senha deve ter pelo menos 8 caracteres.")
        String password,
        @NotNull(message = "Informe o perfil.")
        UserRole role,
        Boolean active
) {
}
