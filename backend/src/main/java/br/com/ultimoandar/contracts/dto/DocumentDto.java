package br.com.ultimoandar.contracts.dto;

import br.com.ultimoandar.contracts.entity.enums.DocumentType;
import java.time.Instant;
import java.util.UUID;

public record DocumentDto(
        UUID id,
        DocumentType documentType,
        UUID contractId,
        String contractNumber,
        String title,
        String fileName,
        String contentType,
        long sizeBytes,
        Instant generatedAt,
        boolean archived
) {
}
