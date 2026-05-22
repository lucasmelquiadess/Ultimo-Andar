package br.com.ultimoandar.contracts.service;

import br.com.ultimoandar.contracts.dto.OwnerDto;
import br.com.ultimoandar.contracts.entity.Owner;
import br.com.ultimoandar.contracts.exception.BusinessException;
import br.com.ultimoandar.contracts.exception.ResourceNotFoundException;
import br.com.ultimoandar.contracts.mapper.OwnerMapper;
import br.com.ultimoandar.contracts.repository.OwnerRepository;
import br.com.ultimoandar.contracts.util.BrazilianDocumentValidator;
import br.com.ultimoandar.contracts.util.TextNormalizer;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OwnerService {

    private final OwnerRepository repository;
    private final AuditService auditService;

    public OwnerService(OwnerRepository repository, AuditService auditService) {
        this.repository = repository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<OwnerDto> list(String search, Boolean active) {
        return repository.search(TextNormalizer.search(search), active).stream().map(OwnerMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public Owner getEntity(UUID id) {
        return repository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ResourceNotFoundException("Locador não encontrado."));
    }

    @Transactional(readOnly = true)
    public OwnerDto get(UUID id) {
        return OwnerMapper.toDto(getEntity(id));
    }

    @Transactional
    public OwnerDto create(OwnerDto dto) {
        String document = normalizeDocument(dto.document());
        if (repository.existsByDocument(document)) {
            throw new BusinessException("Já existe um locador com este CPF/CNPJ.");
        }
        Owner owner = new Owner();
        OwnerMapper.copyToEntity(dto, owner);
        owner.setDocument(document);
        Owner saved = repository.save(owner);
        auditService.record("OWNER_CREATED", "OWNER", saved.getId(), "Locador " + saved.getName());
        return OwnerMapper.toDto(saved);
    }

    @Transactional
    public OwnerDto update(UUID id, OwnerDto dto) {
        Owner owner = getEntity(id);
        String document = normalizeDocument(dto.document());
        if (repository.existsByDocumentAndIdNot(document, id)) {
            throw new BusinessException("Já existe outro locador com este CPF/CNPJ.");
        }
        OwnerMapper.copyToEntity(dto, owner);
        owner.setDocument(document);
        Owner saved = repository.save(owner);
        auditService.record("OWNER_UPDATED", "OWNER", saved.getId(), "Locador " + saved.getName());
        return OwnerMapper.toDto(saved);
    }

    @Transactional
    public void deactivate(UUID id) {
        Owner owner = getEntity(id);
        owner.setActive(false);
        repository.save(owner);
        auditService.record("OWNER_DEACTIVATED", "OWNER", owner.getId(), "Locador " + owner.getName());
    }

    private String normalizeDocument(String document) {
        if (!BrazilianDocumentValidator.isCpfOrCnpj(document)) {
            throw new BusinessException("Informe um CPF ou CNPJ válido.");
        }
        return BrazilianDocumentValidator.onlyDigits(document);
    }
}
