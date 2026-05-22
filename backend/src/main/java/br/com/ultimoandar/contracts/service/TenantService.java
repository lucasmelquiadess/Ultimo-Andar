package br.com.ultimoandar.contracts.service;

import br.com.ultimoandar.contracts.dto.TenantDto;
import br.com.ultimoandar.contracts.entity.Tenant;
import br.com.ultimoandar.contracts.exception.BusinessException;
import br.com.ultimoandar.contracts.exception.ResourceNotFoundException;
import br.com.ultimoandar.contracts.mapper.TenantMapper;
import br.com.ultimoandar.contracts.repository.TenantRepository;
import br.com.ultimoandar.contracts.util.BrazilianDocumentValidator;
import br.com.ultimoandar.contracts.util.TextNormalizer;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TenantService {

    private final TenantRepository repository;
    private final AuditService auditService;

    public TenantService(TenantRepository repository, AuditService auditService) {
        this.repository = repository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<TenantDto> list(String search, Boolean active) {
        return repository.search(TextNormalizer.search(search), active).stream().map(TenantMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public Tenant getEntity(UUID id) {
        return repository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ResourceNotFoundException("Locatário não encontrado."));
    }

    @Transactional(readOnly = true)
    public TenantDto get(UUID id) {
        return TenantMapper.toDto(getEntity(id));
    }

    @Transactional
    public TenantDto create(TenantDto dto) {
        String document = normalizeDocument(dto.document());
        if (repository.existsByDocument(document)) {
            throw new BusinessException("Já existe um locatário com este CPF/CNPJ.");
        }
        Tenant tenant = new Tenant();
        TenantMapper.copyToEntity(dto, tenant);
        tenant.setDocument(document);
        Tenant saved = repository.save(tenant);
        auditService.record("TENANT_CREATED", "TENANT", saved.getId(), "Locatário " + saved.getName());
        return TenantMapper.toDto(saved);
    }

    @Transactional
    public TenantDto update(UUID id, TenantDto dto) {
        Tenant tenant = getEntity(id);
        String document = normalizeDocument(dto.document());
        if (repository.existsByDocumentAndIdNot(document, id)) {
            throw new BusinessException("Já existe outro locatário com este CPF/CNPJ.");
        }
        TenantMapper.copyToEntity(dto, tenant);
        tenant.setDocument(document);
        Tenant saved = repository.save(tenant);
        auditService.record("TENANT_UPDATED", "TENANT", saved.getId(), "Locatário " + saved.getName());
        return TenantMapper.toDto(saved);
    }

    @Transactional
    public void deactivate(UUID id) {
        Tenant tenant = getEntity(id);
        tenant.setActive(false);
        repository.save(tenant);
        auditService.record("TENANT_DEACTIVATED", "TENANT", tenant.getId(), "Locatário " + tenant.getName());
    }

    private String normalizeDocument(String document) {
        if (!BrazilianDocumentValidator.isCpfOrCnpj(document)) {
            throw new BusinessException("Informe um CPF ou CNPJ válido.");
        }
        return BrazilianDocumentValidator.onlyDigits(document);
    }
}
