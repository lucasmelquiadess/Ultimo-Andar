package br.com.ultimoandar.contracts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TerminationRequest(
        @NotNull(message = "Selecione um contrato.")
        UUID contractId,
        @NotNull(message = "Informe a data de encerramento.")
        LocalDate terminationDate,
        @NotBlank(message = "Informe o motivo do encerramento.")
        String reason,
        boolean hasPendingDebts,
        BigDecimal penaltyAmount,
        BigDecimal proportionalRentAmount,
        BigDecimal pendingChargesAmount,
        BigDecimal repairsAmount,
        String observations,
        String additionalStatements
) {
}
