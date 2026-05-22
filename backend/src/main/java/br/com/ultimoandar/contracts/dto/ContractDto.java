package br.com.ultimoandar.contracts.dto;

import br.com.ultimoandar.contracts.entity.enums.ContractStatus;
import br.com.ultimoandar.contracts.entity.enums.GuaranteeType;
import br.com.ultimoandar.contracts.entity.enums.LeaseTermType;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ContractDto(
        UUID id,
        String contractNumber,
        UUID propertyId,
        String propertyCode,
        String propertyAddress,
        UUID ownerId,
        String ownerName,
        UUID tenantId,
        String tenantName,
        BigDecimal monthlyRent,
        Integer rentDueDay,
        LeaseTermType termType,
        LocalDate startDate,
        LocalDate endDate,
        String adjustmentIndex,
        GuaranteeType guaranteeType,
        String paymentMethod,
        String notes,
        String extraClauses,
        ContractStatus status,
        Instant generatedAt,
        Instant createdAt,
        Instant updatedAt
) {
}
