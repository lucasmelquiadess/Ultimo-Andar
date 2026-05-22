package br.com.ultimoandar.contracts.storage;

public record StoredFile(
        String fileName,
        String storagePath,
        String contentType,
        long sizeBytes
) {
}
