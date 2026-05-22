package br.com.ultimoandar.contracts.controller;

import br.com.ultimoandar.contracts.dto.DocumentDto;
import br.com.ultimoandar.contracts.entity.GeneratedDocument;
import br.com.ultimoandar.contracts.entity.enums.DocumentType;
import br.com.ultimoandar.contracts.service.DocumentService;
import java.util.List;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService service;

    public DocumentController(DocumentService service) {
        this.service = service;
    }

    @GetMapping
    public List<DocumentDto> list(
            @RequestParam(required = false) DocumentType type,
            @RequestParam(required = false) UUID contractId,
            @RequestParam(required = false) String search
    ) {
        return service.list(type, contractId, search);
    }

    @GetMapping("/{id}")
    public DocumentDto get(@PathVariable UUID id) {
        return service.get(id);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable UUID id) {
        GeneratedDocument document = service.getEntity(id);
        Resource resource = service.loadFile(id);
        String contentType = document.getContentType();
        if (contentType == null || contentType.isBlank()) {
            contentType = MediaType.APPLICATION_PDF_VALUE;
        }
        String fileName = document.getFileName();
        if (fileName == null || fileName.isBlank()) {
            fileName = "documento.pdf";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline()
                        .filename(fileName)
                        .build()
                        .toString())
                .body(resource);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archive(@PathVariable UUID id) {
        service.archive(id);
    }
}
