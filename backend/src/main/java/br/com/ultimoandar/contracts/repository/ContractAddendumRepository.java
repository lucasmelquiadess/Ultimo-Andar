package br.com.ultimoandar.contracts.repository;

import br.com.ultimoandar.contracts.entity.ContractAddendum;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractAddendumRepository extends JpaRepository<ContractAddendum, UUID> {

    List<ContractAddendum> findByContractIdOrderByCreatedAtDesc(UUID contractId);
}
