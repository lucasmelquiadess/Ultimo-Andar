package br.com.ultimoandar.contracts.service;

import br.com.ultimoandar.contracts.dto.DashboardDto;
import br.com.ultimoandar.contracts.entity.LeaseContract;
import br.com.ultimoandar.contracts.entity.enums.ContractStatus;
import br.com.ultimoandar.contracts.mapper.ContractMapper;
import br.com.ultimoandar.contracts.repository.GeneratedDocumentRepository;
import br.com.ultimoandar.contracts.repository.LeaseContractRepository;
import br.com.ultimoandar.contracts.repository.OwnerRepository;
import br.com.ultimoandar.contracts.repository.RentalPropertyRepository;
import br.com.ultimoandar.contracts.repository.TenantRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

    private final RentalPropertyRepository propertyRepository;
    private final OwnerRepository ownerRepository;
    private final TenantRepository tenantRepository;
    private final LeaseContractRepository contractRepository;
    private final GeneratedDocumentRepository documentRepository;

    public DashboardService(
            RentalPropertyRepository propertyRepository,
            OwnerRepository ownerRepository,
            TenantRepository tenantRepository,
            LeaseContractRepository contractRepository,
            GeneratedDocumentRepository documentRepository
    ) {
        this.propertyRepository = propertyRepository;
        this.ownerRepository = ownerRepository;
        this.tenantRepository = tenantRepository;
        this.contractRepository = contractRepository;
        this.documentRepository = documentRepository;
    }

    @Transactional(readOnly = true)
    public DashboardDto get() {
        LocalDate today = LocalDate.now();
        BigDecimal monthlyRentPortfolio = contractRepository.sumActiveMonthlyRent();
        return new DashboardDto(
                propertyRepository.count(),
                ownerRepository.count(),
                tenantRepository.count(),
                contractRepository.countByStatus(ContractStatus.ACTIVE),
                monthlyRentPortfolio == null ? BigDecimal.ZERO : monthlyRentPortfolio,
                contractRepository.findExpiring(today, today.plusDays(90)).stream()
                        .limit(6)
                        .map(this::expiring)
                        .toList(),
                documentRepository.findTop8ByArchivedFalseOrderByGeneratedAtDesc().stream()
                        .map(ContractMapper::documentToDto)
                        .toList()
        );
    }

    private DashboardDto.ExpiringContractDto expiring(LeaseContract contract) {
        return new DashboardDto.ExpiringContractDto(
                contract.getContractNumber(),
                contract.getTenant().getName(),
                contract.getProperty().getAddress().formatted(),
                contract.getEndDate()
        );
    }
}
