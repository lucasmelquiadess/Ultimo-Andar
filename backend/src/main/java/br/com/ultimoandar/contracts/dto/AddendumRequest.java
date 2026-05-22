package br.com.ultimoandar.contracts.dto;

import br.com.ultimoandar.contracts.entity.enums.AddendumType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record AddendumRequest(
        @NotNull(message = "Selecione um contrato.")
        UUID contractId,
        @NotNull(message = "Informe o tipo do aditivo.")
        AddendumType addendumType,
        @NotBlank(message = "Descreva a alteração do aditivo.")
        String description,
        @NotNull(message = "Informe a data do aditivo.")
        LocalDate addendumDate,
        BigDecimal newMonthlyRent,
        String newTerm,
        LocalDate newEndDate,
        String specificChanges,
        String observations
) {
}
