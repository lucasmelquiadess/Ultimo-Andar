package br.com.ultimoandar.contracts.repository;

import br.com.ultimoandar.contracts.entity.Owner;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OwnerRepository extends JpaRepository<Owner, UUID> {

    boolean existsByDocumentAndIdNot(String document, UUID id);

    boolean existsByDocument(String document);

    @Query("""
            select o from Owner o
            where (:active is null or o.active = :active)
              and (:search is null or lower(o.name) like :search or lower(o.document) like :search)
            order by o.createdAt desc
            """)
    List<Owner> search(@Param("search") String search, @Param("active") Boolean active);
}
