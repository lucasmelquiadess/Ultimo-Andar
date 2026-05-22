package br.com.ultimoandar.contracts.repository;

import br.com.ultimoandar.contracts.entity.AuditEvent;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuditEventRepository extends JpaRepository<AuditEvent, UUID> {

    List<AuditEvent> findByOrderByCreatedAtDesc(Pageable pageable);

    @Query("""
            select e from AuditEvent e
            where (:actor is null or lower(e.actor) like :actor)
              and (:action is null or lower(e.action) like :action)
              and (:resourceType is null or lower(e.resourceType) like :resourceType)
            order by e.createdAt desc
            """)
    List<AuditEvent> search(
            @Param("actor") String actor,
            @Param("action") String action,
            @Param("resourceType") String resourceType,
            Pageable pageable
    );

    long deleteByCreatedAtBefore(Instant cutoff);
}
