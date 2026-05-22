package br.com.ultimoandar.contracts.dto;

import br.com.ultimoandar.contracts.entity.enums.PropertyStatus;
import br.com.ultimoandar.contracts.entity.enums.PropertyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PropertyDto(
        UUID id,
        @NotBlank(message = "Informe o código interno do imóvel.")
        String code,
        @NotNull(message = "Informe o tipo do imóvel.")
        PropertyType type,
        AddressDto address,
        String description,
        @NotNull(message = "Informe o valor mensal do aluguel.")
        @PositiveOrZero(message = "O valor mensal deve ser maior ou igual a zero.")
        BigDecimal monthlyRent,
        BigDecimal condominiumFee,
        BigDecimal iptuValue,
        @NotNull(message = "Informe o status do imóvel.")
        PropertyStatus status,
        @NotNull(message = "Vincule um locador ao imóvel.")
        UUID ownerId,
        String ownerName,
        String internalNotes,
        List<PropertyPhotoDto> photos,
        Instant createdAt,
        Instant updatedAt
) {
}
