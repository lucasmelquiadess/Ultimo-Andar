package br.com.ultimoandar.contracts.dto;

import br.com.ultimoandar.contracts.entity.enums.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record TemplateDto(
        UUID id,
        @NotNull(message = "Informe o tipo de documento.")
        DocumentType documentType,
        @NotBlank(message = "Informe o nome do modelo.")
        String name,
        @NotBlank(message = "Informe o conteúdo HTML do modelo.")
        String content,
        Boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
