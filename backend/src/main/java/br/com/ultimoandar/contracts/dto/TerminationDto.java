package br.com.ultimoandar.contracts.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TerminationDto(
        UUID id,
        UUID contractId,
        String contractNumber,
        LocalDate terminationDate,
        String reason,
        boolean hasPendingDebts,
        BigDecimal penaltyAmount,
        BigDecimal proportionalRentAmount,
        BigDecimal pendingChargesAmount,
        BigDecimal repairsAmount,
        String observations,
        String additionalStatements,
        Instant createdAt
) {
}
