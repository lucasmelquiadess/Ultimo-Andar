package br.com.ultimoandar.contracts.dto;

import br.com.ultimoandar.contracts.entity.enums.PersonType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record OwnerDto(
        UUID id,
        @NotNull(message = "Informe o tipo de pessoa.")
        PersonType personType,
        @NotBlank(message = "Informe o nome do locador.")
        String name,
        @NotBlank(message = "Informe o CPF ou CNPJ do locador.")
        String document,
        String identityNumber,
        String nationality,
        String maritalStatus,
        String profession,
        AddressDto address,
        String phone,
        @Email(message = "Informe um e-mail válido.")
        String email,
        String bankDetails,
        String notes,
        Boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
