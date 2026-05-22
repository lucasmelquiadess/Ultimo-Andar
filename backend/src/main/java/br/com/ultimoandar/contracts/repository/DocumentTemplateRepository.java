package br.com.ultimoandar.contracts.repository;

import br.com.ultimoandar.contracts.entity.DocumentTemplate;
import br.com.ultimoandar.contracts.entity.enums.DocumentType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentTemplateRepository extends JpaRepository<DocumentTemplate, UUID> {

    Optional<DocumentTemplate> findFirstByDocumentTypeAndActiveTrueOrderByUpdatedAtDesc(DocumentType documentType);

    List<DocumentTemplate> findByDocumentTypeOrderByUpdatedAtDesc(DocumentType documentType);
}
