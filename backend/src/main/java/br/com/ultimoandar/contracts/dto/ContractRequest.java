package br.com.ultimoandar.contracts.dto;

import br.com.ultimoandar.contracts.entity.enums.GuaranteeType;
import br.com.ultimoandar.contracts.entity.enums.LeaseTermType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ContractRequest(
        @NotNull(message = "Selecione o imóvel.")
        UUID propertyId,
        @NotNull(message = "Selecione o locador.")
        UUID ownerId,
        @NotNull(message = "Selecione o locatário.")
        UUID tenantId,
        @NotNull(message = "Informe o valor mensal do aluguel.")
        @Positive(message = "O aluguel deve ser maior que zero.")
        BigDecimal monthlyRent,
        @NotNull(message = "Informe o dia de vencimento.")
        @Min(value = 1, message = "O vencimento deve ser entre os dias 1 e 10.")
        @Max(value = 10, message = "A data de vencimento do aluguel deve ser até o dia 10 de cada mês.")
        Integer rentDueDay,
        @NotNull(message = "Informe o prazo do contrato.")
        LeaseTermType termType,
        @NotNull(message = "Informe a data de início.")
        LocalDate startDate,
        String adjustmentIndex,
        @NotNull(message = "Informe a garantia locatícia.")
        GuaranteeType guaranteeType,
        String paymentMethod,
        String notes,
        String extraClauses
) {
}
