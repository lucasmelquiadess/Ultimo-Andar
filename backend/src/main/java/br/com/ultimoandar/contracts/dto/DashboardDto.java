package br.com.ultimoandar.contracts.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record DashboardDto(
        long totalProperties,
        long totalOwners,
        long totalTenants,
        long activeContracts,
        BigDecimal monthlyRentPortfolio,
        List<ExpiringContractDto> expiringContracts,
        List<DocumentDto> recentDocuments
) {
    public record ExpiringContractDto(
            String contractNumber,
            String tenantName,
            String propertyAddress,
            LocalDate endDate
    ) {
    }

    public record ActivityDto(
            String title,
            Instant createdAt
    ) {
    }
}
