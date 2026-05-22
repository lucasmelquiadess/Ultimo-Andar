package br.com.ultimoandar.contracts.repository;

import br.com.ultimoandar.contracts.entity.Tenant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TenantRepository extends JpaRepository<Tenant, UUID> {

    boolean existsByDocumentAndIdNot(String document, UUID id);

    boolean existsByDocument(String document);

    @Query("""
            select t from Tenant t
            where (:active is null or t.active = :active)
              and (:search is null or lower(t.name) like :search or lower(t.document) like :search)
            order by t.createdAt desc
            """)
    List<Tenant> search(@Param("search") String search, @Param("active") Boolean active);
}
