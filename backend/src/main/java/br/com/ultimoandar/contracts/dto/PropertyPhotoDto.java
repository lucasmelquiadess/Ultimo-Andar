package br.com.ultimoandar.contracts.dto;

import java.time.Instant;
import java.util.UUID;

public record PropertyPhotoDto(
        UUID id,
        String fileName,
        String originalFileName,
        String contentType,
        long sizeBytes,
        Instant createdAt
) {
}
