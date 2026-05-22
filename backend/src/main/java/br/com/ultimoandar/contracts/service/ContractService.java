package br.com.ultimoandar.contracts.service;

import br.com.ultimoandar.contracts.document.PlaceholderFactory;
import br.com.ultimoandar.contracts.dto.ContractDto;
import br.com.ultimoandar.contracts.dto.ContractRequest;
import br.com.ultimoandar.contracts.entity.LeaseContract;
import br.com.ultimoandar.contracts.entity.Owner;
import br.com.ultimoandar.contracts.entity.RentalProperty;
import br.com.ultimoandar.contracts.entity.Tenant;
import br.com.ultimoandar.contracts.entity.enums.ContractStatus;
import br.com.ultimoandar.contracts.entity.enums.DocumentType;
import br.com.ultimoandar.contracts.entity.enums.LeaseTermType;
import br.com.ultimoandar.contracts.entity.enums.PropertyStatus;
import br.com.ultimoandar.contracts.exception.BusinessException;
import br.com.ultimoandar.contracts.exception.ResourceNotFoundException;
import br.com.ultimoandar.contracts.mapper.ContractMapper;
import br.com.ultimoandar.contracts.repository.LeaseContractRepository;
import br.com.ultimoandar.contracts.util.TextNormalizer;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContractService {

    private final LeaseContractRepository repository;
    private final PropertyService propertyService;
    private final OwnerService ownerService;
    private final TenantService tenantService;
    private final DocumentService documentService;
    private final PlaceholderFactory placeholderFactory;
    private final AuditService auditService;

    public ContractService(
            LeaseContractRepository repository,
            PropertyService propertyService,
            OwnerService ownerService,
            TenantService tenantService,
            DocumentService documentService,
            PlaceholderFactory placeholderFactory,
            AuditService auditService
    ) {
        this.repository = repository;
        this.propertyService = propertyService;
        this.ownerService = ownerService;
        this.tenantService = tenantService;
        this.documentService = documentService;
        this.placeholderFactory = placeholderFactory;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<ContractDto> list(String search, ContractStatus status) {
        return repository.search(TextNormalizer.search(search), status).stream().map(ContractMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public LeaseContract getEntity(UUID id) {
        return repository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ResourceNotFoundException("Contrato não encontrado."));
    }

    @Transactional(readOnly = true)
    public ContractDto get(UUID id) {
        return ContractMapper.toDto(getEntity(id));
    }

    @Transactional
    public ContractDto generate(ContractRequest request) {
        validateDueDay(request.rentDueDay());
        RentalProperty property = propertyService.getEntity(request.propertyId());
        Owner owner = ownerService.getEntity(request.ownerId());
        Tenant tenant = tenantService.getEntity(request.tenantId());
        if (!property.getOwner().getId().equals(owner.getId())) {
            throw new BusinessException("O locador selecionado não é o proprietário vinculado ao imóvel.");
        }

        LeaseContract contract = new LeaseContract();
        contract.setContractNumber(nextContractNumber());
        contract.setProperty(property);
        contract.setOwner(owner);
        contract.setTenant(tenant);
        contract.setMonthlyRent(request.monthlyRent());
        contract.setRentDueDay(request.rentDueDay());
        contract.setTermType(request.termType());
        contract.setStartDate(request.startDate());
        contract.setEndDate(calculateEndDate(request.startDate(), request.termType()));
        contract.setAdjustmentIndex(request.adjustmentIndex());
        contract.setGuaranteeType(request.guaranteeType());
        contract.setPaymentMethod(request.paymentMethod());
        contract.setNotes(request.notes());
        contract.setExtraClauses(request.extraClauses());
        contract.setStatus(ContractStatus.ACTIVE);
        contract.setGeneratedAt(Instant.now());
        LeaseContract saved = repository.save(contract);
        property.setStatus(PropertyStatus.RENTED);

        documentService.generateAndStore(
                DocumentType.LEASE_CONTRACT,
                saved,
                saved.getId(),
                "Contrato de locação " + saved.getContractNumber(),
                "contrato-" + saved.getContractNumber(),
                placeholderFactory.forContract(saved)
        );
        auditService.record("CONTRACT_GENERATED", "CONTRACT", saved.getId(), "Contrato " + saved.getContractNumber());
        return ContractMapper.toDto(saved);
    }

    @Transactional
    public ContractDto reissue(UUID id) {
        LeaseContract contract = getEntity(id);
        contract.setGeneratedAt(Instant.now());
        documentService.generateAndStore(
                DocumentType.LEASE_CONTRACT,
                contract,
                contract.getId(),
                "Reemissão do contrato " + contract.getContractNumber(),
                "contrato-" + contract.getContractNumber() + "-reemissao-" + System.currentTimeMillis(),
                placeholderFactory.forContract(contract)
        );
        LeaseContract saved = repository.save(contract);
        auditService.record("CONTRACT_REISSUED", "CONTRACT", saved.getId(), "Contrato " + saved.getContractNumber());
        return ContractMapper.toDto(saved);
    }

    @Transactional
    public ContractDto updateStatus(UUID id, ContractStatus status) {
        LeaseContract contract = getEntity(id);
        contract.setStatus(status);
        LeaseContract saved = repository.save(contract);
        auditService.record("CONTRACT_STATUS_UPDATED", "CONTRACT", saved.getId(), "Status " + status);
        return ContractMapper.toDto(saved);
    }

    private void validateDueDay(Integer rentDueDay) {
        if (rentDueDay == null || rentDueDay < 1 || rentDueDay > 10) {
            throw new BusinessException("A data de vencimento do aluguel deve ser até o dia 10 de cada mês.");
        }
    }

    private LocalDate calculateEndDate(LocalDate startDate, LeaseTermType termType) {
        return switch (termType) {
            case MONTHS_12 -> startDate.plusMonths(12).minusDays(1);
            case MONTHS_24 -> startDate.plusMonths(24).minusDays(1);
            case MONTHS_36 -> startDate.plusMonths(36).minusDays(1);
            case INDETERMINATE -> null;
        };
    }

    private String nextContractNumber() {
        String prefix = "UA-" + Year.now().getValue() + "-";
        long next = repository.countByContractNumberStartingWith(prefix) + 1;
        return prefix + String.format("%05d", next);
    }
}
