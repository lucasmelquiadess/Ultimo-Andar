package br.com.ultimoandar.contracts.repository;

import br.com.ultimoandar.contracts.entity.ContractTermination;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractTerminationRepository extends JpaRepository<ContractTermination, UUID> {

    List<ContractTermination> findByContractIdOrderByCreatedAtDesc(UUID contractId);
}
