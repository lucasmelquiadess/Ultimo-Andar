package br.com.ultimoandar.contracts.repository;

import br.com.ultimoandar.contracts.entity.PropertyPhoto;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyPhotoRepository extends JpaRepository<PropertyPhoto, UUID> {
}
