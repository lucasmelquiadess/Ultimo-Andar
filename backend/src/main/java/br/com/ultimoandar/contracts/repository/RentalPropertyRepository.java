package br.com.ultimoandar.contracts.repository;

import br.com.ultimoandar.contracts.entity.RentalProperty;
import br.com.ultimoandar.contracts.entity.enums.PropertyStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RentalPropertyRepository extends JpaRepository<RentalProperty, UUID> {

    boolean existsByCodeAndIdNot(String code, UUID id);

    boolean existsByCode(String code);

    long countByStatus(PropertyStatus status);

    @Query("""
            select p from RentalProperty p
            join fetch p.owner o
            where (:status is null or p.status = :status)
              and (:ownerId is null or o.id = :ownerId)
              and (:city is null or lower(p.address.city) like :city)
              and (:search is null or lower(p.code) like :search or lower(p.address.street) like :search)
            order by p.createdAt desc
            """)
    List<RentalProperty> search(
            @Param("search") String search,
            @Param("status") PropertyStatus status,
            @Param("city") String city,
            @Param("ownerId") UUID ownerId
    );
}
