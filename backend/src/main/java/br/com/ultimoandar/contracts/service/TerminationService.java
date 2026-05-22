package br.com.ultimoandar.contracts.service;

import br.com.ultimoandar.contracts.document.PlaceholderFactory;
import br.com.ultimoandar.contracts.dto.TerminationDto;
import br.com.ultimoandar.contracts.dto.TerminationRequest;
import br.com.ultimoandar.contracts.entity.ContractTermination;
import br.com.ultimoandar.contracts.entity.LeaseContract;
import br.com.ultimoandar.contracts.entity.enums.ContractStatus;
import br.com.ultimoandar.contracts.entity.enums.DocumentType;
import br.com.ultimoandar.contracts.entity.enums.PropertyStatus;
import br.com.ultimoandar.contracts.mapper.ContractMapper;
import br.com.ultimoandar.contracts.repository.ContractTerminationRepository;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TerminationService {

    private final ContractTerminationRepository repository;
    private final ContractService contractService;
    private final DocumentService documentService;
    private final PlaceholderFactory placeholderFactory;
    private final AuditService auditService;

    public TerminationService(
            ContractTerminationRepository repository,
            ContractService contractService,
            DocumentService documentService,
            PlaceholderFactory placeholderFactory,
            AuditService auditService
    ) {
        this.repository = repository;
        this.contractService = contractService;
        this.documentService = documentService;
        this.placeholderFactory = placeholderFactory;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<TerminationDto> list(UUID contractId) {
        List<ContractTermination> terminations = contractId == null
                ? repository.findAll().stream().sorted(Comparator.comparing(ContractTermination::getCreatedAt).reversed()).toList()
                : repository.findByContractIdOrderByCreatedAtDesc(contractId);
        return terminations.stream()
                .map(ContractMapper::terminationToDto)
                .toList();
    }

    @Transactional
    public TerminationDto generate(TerminationRequest request) {
        LeaseContract contract = contractService.getEntity(request.contractId());
        ContractTermination termination = new ContractTermination();
        termination.setContract(contract);
        termination.setTerminationDate(request.terminationDate());
        termination.setReason(request.reason());
        termination.setHasPendingDebts(request.hasPendingDebts());

        DebtAmounts debts = calculateDebts(contract, request);
        termination.setPenaltyAmount(debts.penalty());
        termination.setProportionalRentAmount(debts.proportionalRent());
        termination.setPendingChargesAmount(debts.pendingCharges());
        termination.setRepairsAmount(debts.repairs());
        termination.setObservations(request.observations());
        termination.setAdditionalStatements(request.additionalStatements());
        ContractTermination saved = repository.save(termination);

        contract.setStatus(ContractStatus.CLOSED);
        contract.getProperty().setStatus(PropertyStatus.AVAILABLE);

        documentService.generateAndStore(
                DocumentType.TERMINATION,
                contract,
                saved.getId(),
                "Distrato do contrato " + contract.getContractNumber(),
                "distrato-" + contract.getContractNumber() + "-" + saved.getId(),
                placeholderFactory.forTermination(saved)
        );
        auditService.record("TERMINATION_GENERATED", "TERMINATION", saved.getId(), "Contrato " + contract.getContractNumber());
        return ContractMapper.terminationToDto(saved);
    }

    private BigDecimal zero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private DebtAmounts calculateDebts(LeaseContract contract, TerminationRequest request) {
        if (!request.hasPendingDebts()) {
            return new DebtAmounts(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        BigDecimal monthlyRent = zero(contract.getMonthlyRent());
        BigDecimal penalty = zero(request.penaltyAmount());
        BigDecimal proportionalRent = zero(request.proportionalRentAmount());
        BigDecimal repairs = zero(request.repairsAmount());
        BigDecimal maxPenalty = monthlyRent.multiply(new BigDecimal("0.20"));

        if (penalty.compareTo(maxPenalty) > 0) {
            throw new br.com.ultimoandar.contracts.exception.BusinessException("A multa do distrato deve ser limitada a 20% do aluguel.");
        }
        if (monthlyRent.compareTo(BigDecimal.ZERO) > 0 && !isAllowedProportionalRent(monthlyRent, proportionalRent)) {
            throw new br.com.ultimoandar.contracts.exception.BusinessException("O aluguel proporcional deve corresponder a 1, 2 ou 3 aluguéis.");
        }

        return new DebtAmounts(
                penalty,
                proportionalRent,
                penalty.add(proportionalRent).add(repairs),
                repairs
        );
    }

    private boolean isAllowedProportionalRent(BigDecimal monthlyRent, BigDecimal proportionalRent) {
        return proportionalRent.compareTo(monthlyRent) == 0
                || proportionalRent.compareTo(monthlyRent.multiply(BigDecimal.valueOf(2))) == 0
                || proportionalRent.compareTo(monthlyRent.multiply(BigDecimal.valueOf(3))) == 0;
    }

    private record DebtAmounts(
            BigDecimal penalty,
            BigDecimal proportionalRent,
            BigDecimal pendingCharges,
            BigDecimal repairs
    ) {
    }
}
