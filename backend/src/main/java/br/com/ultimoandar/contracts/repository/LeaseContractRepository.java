package br.com.ultimoandar.contracts.repository;

import br.com.ultimoandar.contracts.entity.LeaseContract;
import br.com.ultimoandar.contracts.entity.enums.ContractStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LeaseContractRepository extends JpaRepository<LeaseContract, UUID> {

    long countByStatus(ContractStatus status);

    long countByContractNumberStartingWith(String prefix);

    @Query("""
            select sum(c.monthlyRent) from LeaseContract c
            where c.status = br.com.ultimoandar.contracts.entity.enums.ContractStatus.ACTIVE
            """)
    java.math.BigDecimal sumActiveMonthlyRent();

    @Query("""
            select c from LeaseContract c
            join fetch c.property p
            join fetch c.owner o
            join fetch c.tenant t
            where (:status is null or c.status = :status)
              and (:search is null or lower(c.contractNumber) like :search or lower(t.name) like :search or lower(o.name) like :search or lower(p.code) like :search)
            order by c.createdAt desc
            """)
    List<LeaseContract> search(@Param("search") String search, @Param("status") ContractStatus status);

    @Query("""
            select c from LeaseContract c
            join fetch c.property p
            join fetch c.tenant t
            where c.status = br.com.ultimoandar.contracts.entity.enums.ContractStatus.ACTIVE
              and c.endDate between :today and :limit
            order by c.endDate asc
            """)
    List<LeaseContract> findExpiring(@Param("today") LocalDate today, @Param("limit") LocalDate limit);
}
