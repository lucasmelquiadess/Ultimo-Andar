package br.com.ultimoandar.contracts.dto;

import br.com.ultimoandar.contracts.entity.enums.AddendumType;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record AddendumDto(
        UUID id,
        UUID contractId,
        String contractNumber,
        AddendumType addendumType,
        String description,
        LocalDate addendumDate,
        BigDecimal newMonthlyRent,
        String newTerm,
        LocalDate newEndDate,
        String specificChanges,
        String observations,
        Instant createdAt
) {
}
