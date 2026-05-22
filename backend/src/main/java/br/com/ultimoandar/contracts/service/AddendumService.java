package br.com.ultimoandar.contracts.service;

import br.com.ultimoandar.contracts.document.PlaceholderFactory;
import br.com.ultimoandar.contracts.dto.AddendumDto;
import br.com.ultimoandar.contracts.dto.AddendumRequest;
import br.com.ultimoandar.contracts.entity.ContractAddendum;
import br.com.ultimoandar.contracts.entity.LeaseContract;
import br.com.ultimoandar.contracts.entity.enums.DocumentType;
import br.com.ultimoandar.contracts.mapper.ContractMapper;
import br.com.ultimoandar.contracts.repository.ContractAddendumRepository;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddendumService {

    private final ContractAddendumRepository repository;
    private final ContractService contractService;
    private final DocumentService documentService;
    private final PlaceholderFactory placeholderFactory;
    private final AuditService auditService;

    public AddendumService(
            ContractAddendumRepository repository,
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
    public List<AddendumDto> list(UUID contractId) {
        List<ContractAddendum> addendums = contractId == null
                ? repository.findAll().stream().sorted(Comparator.comparing(ContractAddendum::getCreatedAt).reversed()).toList()
                : repository.findByContractIdOrderByCreatedAtDesc(contractId);
        return addendums.stream()
                .map(ContractMapper::addendumToDto)
                .toList();
    }

    @Transactional
    public AddendumDto generate(AddendumRequest request) {
        LeaseContract contract = contractService.getEntity(request.contractId());
        ContractAddendum addendum = new ContractAddendum();
        addendum.setContract(contract);
        addendum.setAddendumType(request.addendumType());
        addendum.setDescription(request.description());
        addendum.setAddendumDate(request.addendumDate());
        addendum.setNewMonthlyRent(request.newMonthlyRent());
        addendum.setNewTerm(request.newTerm());
        addendum.setNewEndDate(request.newEndDate());
        addendum.setSpecificChanges(request.specificChanges());
        addendum.setObservations(request.observations());
        ContractAddendum saved = repository.save(addendum);

        documentService.generateAndStore(
                DocumentType.ADDENDUM,
                contract,
                saved.getId(),
                "Aditivo do contrato " + contract.getContractNumber(),
                "aditivo-" + contract.getContractNumber() + "-" + saved.getId(),
                placeholderFactory.forAddendum(saved)
        );
        auditService.record("ADDENDUM_GENERATED", "ADDENDUM", saved.getId(), "Contrato " + contract.getContractNumber());
        return ContractMapper.addendumToDto(saved);
    }
}
