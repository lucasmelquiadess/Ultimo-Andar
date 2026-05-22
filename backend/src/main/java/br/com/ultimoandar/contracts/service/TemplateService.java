package br.com.ultimoandar.contracts.service;

import br.com.ultimoandar.contracts.dto.TemplateDto;
import br.com.ultimoandar.contracts.entity.DocumentTemplate;
import br.com.ultimoandar.contracts.entity.enums.DocumentType;
import br.com.ultimoandar.contracts.mapper.ContractMapper;
import br.com.ultimoandar.contracts.repository.DocumentTemplateRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TemplateService {

    private final DocumentTemplateRepository repository;
    private final AuditService auditService;

    public TemplateService(DocumentTemplateRepository repository, AuditService auditService) {
        this.repository = repository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<TemplateDto> list(DocumentType type) {
        List<DocumentTemplate> templates = type == null
                ? repository.findAll()
                : repository.findByDocumentTypeOrderByUpdatedAtDesc(type);
        return templates.stream().map(ContractMapper::templateToDto).toList();
    }

    @Transactional
    public TemplateDto create(TemplateDto dto) {
        if (dto.active() == null || dto.active()) {
            repository.findByDocumentTypeOrderByUpdatedAtDesc(dto.documentType())
                    .forEach(template -> template.setActive(false));
        }
        DocumentTemplate template = new DocumentTemplate();
        template.setDocumentType(dto.documentType());
        template.setName(dto.name());
        template.setContent(dto.content());
        template.setActive(dto.active() == null || dto.active());
        DocumentTemplate saved = repository.save(template);
        auditService.record("TEMPLATE_CREATED", "TEMPLATE", saved.getId(), "Modelo " + saved.getName());
        return ContractMapper.templateToDto(saved);
    }
}
