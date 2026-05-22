package br.com.ultimoandar.contracts.service;

import br.com.ultimoandar.contracts.document.PdfGeneratorService;
import br.com.ultimoandar.contracts.document.TemplateRenderService;
import br.com.ultimoandar.contracts.dto.DocumentDto;
import br.com.ultimoandar.contracts.entity.GeneratedDocument;
import br.com.ultimoandar.contracts.entity.LeaseContract;
import br.com.ultimoandar.contracts.entity.enums.DocumentType;
import br.com.ultimoandar.contracts.exception.ResourceNotFoundException;
import br.com.ultimoandar.contracts.mapper.ContractMapper;
import br.com.ultimoandar.contracts.repository.GeneratedDocumentRepository;
import br.com.ultimoandar.contracts.storage.StorageService;
import br.com.ultimoandar.contracts.storage.StoredFile;
import br.com.ultimoandar.contracts.util.TextNormalizer;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DocumentService {

    private final GeneratedDocumentRepository repository;
    private final TemplateRenderService templateRenderService;
    private final PdfGeneratorService pdfGeneratorService;
    private final StorageService storageService;
    private final AuditService auditService;

    public DocumentService(
            GeneratedDocumentRepository repository,
            TemplateRenderService templateRenderService,
            PdfGeneratorService pdfGeneratorService,
            StorageService storageService,
            AuditService auditService
    ) {
        this.repository = repository;
        this.templateRenderService = templateRenderService;
        this.pdfGeneratorService = pdfGeneratorService;
        this.storageService = storageService;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<DocumentDto> list(DocumentType type, UUID contractId, String search) {
        return repository.search(type, contractId, TextNormalizer.search(search)).stream()
                .map(ContractMapper::documentToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public GeneratedDocument getEntity(UUID id) {
        return repository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ResourceNotFoundException("Documento não encontrado."));
    }

    @Transactional(readOnly = true)
    public DocumentDto get(UUID id) {
        return ContractMapper.documentToDto(getEntity(id));
    }

    @Transactional
    public GeneratedDocument generateAndStore(
            DocumentType type,
            LeaseContract contract,
            UUID sourceId,
            String title,
            String baseFileName,
            Map<String, String> placeholders
    ) {
        String html = templateRenderService.render(type, placeholders);
        byte[] pdf = pdfGeneratorService.generate(html);
        StoredFile stored = storageService.storePdf(type.name(), baseFileName, pdf);

        GeneratedDocument document = new GeneratedDocument();
        document.setDocumentType(type);
        document.setContract(contract);
        document.setSourceId(sourceId);
        document.setTitle(title);
        document.setFileName(stored.fileName());
        document.setStoragePath(stored.storagePath());
        document.setContentType(stored.contentType());
        document.setSizeBytes(stored.sizeBytes());
        document.setGeneratedAt(Instant.now());
        return repository.save(document);
    }

    @Transactional(readOnly = true)
    public Resource loadFile(UUID id) {
        GeneratedDocument document = getEntity(id);
        auditService.record("DOCUMENT_DOWNLOADED", "DOCUMENT", document.getId(), "Documento " + document.getTitle());
        return storageService.load(document.getStoragePath());
    }

    @Transactional
    public void archive(UUID id) {
        GeneratedDocument document = getEntity(id);
        document.setArchived(true);
        repository.save(document);
        auditService.record("DOCUMENT_ARCHIVED", "DOCUMENT", document.getId(), "Documento " + document.getTitle());
    }
}
