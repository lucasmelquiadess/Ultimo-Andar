package br.com.ultimoandar.contracts.repository;

import br.com.ultimoandar.contracts.entity.GeneratedDocument;
import br.com.ultimoandar.contracts.entity.enums.DocumentType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GeneratedDocumentRepository extends JpaRepository<GeneratedDocument, UUID> {

    List<GeneratedDocument> findTop8ByArchivedFalseOrderByGeneratedAtDesc();

    @Query("""
            select d from GeneratedDocument d
            left join fetch d.contract c
            left join fetch c.property p
            left join fetch c.owner o
            left join fetch c.tenant t
            where d.archived = false
              and (:type is null or d.documentType = :type)
              and (:contractId is null or c.id = :contractId)
              and (:search is null or lower(d.title) like :search or lower(d.fileName) like :search or lower(c.contractNumber) like :search)
            order by d.generatedAt desc
            """)
    List<GeneratedDocument> search(@Param("type") DocumentType type, @Param("contractId") UUID contractId, @Param("search") String search);

    List<GeneratedDocument> findByContractIdOrderByGeneratedAtDesc(UUID contractId);

    List<GeneratedDocument> findByArchivedFalseOrderByGeneratedAtDesc(Pageable pageable);
}
