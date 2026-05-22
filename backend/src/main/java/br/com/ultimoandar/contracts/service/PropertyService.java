package br.com.ultimoandar.contracts.service;

import br.com.ultimoandar.contracts.dto.PropertyDto;
import br.com.ultimoandar.contracts.dto.PropertyPhotoDto;
import br.com.ultimoandar.contracts.entity.Owner;
import br.com.ultimoandar.contracts.entity.PropertyPhoto;
import br.com.ultimoandar.contracts.entity.RentalProperty;
import br.com.ultimoandar.contracts.entity.enums.PropertyStatus;
import br.com.ultimoandar.contracts.exception.BusinessException;
import br.com.ultimoandar.contracts.exception.ResourceNotFoundException;
import br.com.ultimoandar.contracts.mapper.PropertyMapper;
import br.com.ultimoandar.contracts.repository.PropertyPhotoRepository;
import br.com.ultimoandar.contracts.repository.RentalPropertyRepository;
import br.com.ultimoandar.contracts.storage.StorageService;
import br.com.ultimoandar.contracts.storage.StoredFile;
import br.com.ultimoandar.contracts.util.TextNormalizer;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PropertyService {

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final long MAX_PHOTO_SIZE_BYTES = 8 * 1024 * 1024;

    private final RentalPropertyRepository propertyRepository;
    private final PropertyPhotoRepository photoRepository;
    private final OwnerService ownerService;
    private final StorageService storageService;
    private final AuditService auditService;

    public PropertyService(
            RentalPropertyRepository propertyRepository,
            PropertyPhotoRepository photoRepository,
            OwnerService ownerService,
            StorageService storageService,
            AuditService auditService
    ) {
        this.propertyRepository = propertyRepository;
        this.photoRepository = photoRepository;
        this.ownerService = ownerService;
        this.storageService = storageService;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<PropertyDto> list(String search, PropertyStatus status, String city, UUID ownerId) {
        return propertyRepository.search(
                TextNormalizer.search(search),
                status,
                TextNormalizer.search(city),
                ownerId
        ).stream().map(PropertyMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public RentalProperty getEntity(UUID id) {
        return propertyRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ResourceNotFoundException("Imóvel não encontrado."));
    }

    @Transactional(readOnly = true)
    public PropertyDto get(UUID id) {
        return PropertyMapper.toDto(getEntity(id));
    }

    @Transactional
    public PropertyDto create(PropertyDto dto) {
        if (propertyRepository.existsByCode(dto.code())) {
            throw new BusinessException("Já existe um imóvel com este código interno.");
        }
        RentalProperty property = new RentalProperty();
        Owner owner = ownerService.getEntity(dto.ownerId());
        PropertyMapper.copyToEntity(dto, property);
        property.setOwner(owner);
        RentalProperty saved = propertyRepository.save(property);
        auditService.record("PROPERTY_CREATED", "PROPERTY", saved.getId(), "Imóvel " + saved.getCode());
        return PropertyMapper.toDto(saved);
    }

    @Transactional
    public PropertyDto update(UUID id, PropertyDto dto) {
        RentalProperty property = getEntity(id);
        if (propertyRepository.existsByCodeAndIdNot(dto.code(), id)) {
            throw new BusinessException("Já existe outro imóvel com este código interno.");
        }
        Owner owner = ownerService.getEntity(dto.ownerId());
        PropertyMapper.copyToEntity(dto, property);
        property.setOwner(owner);
        RentalProperty saved = propertyRepository.save(property);
        auditService.record("PROPERTY_UPDATED", "PROPERTY", saved.getId(), "Imóvel " + saved.getCode());
        return PropertyMapper.toDto(saved);
    }

    @Transactional
    public void deactivate(UUID id) {
        RentalProperty property = getEntity(id);
        property.setStatus(PropertyStatus.INACTIVE);
        propertyRepository.save(property);
        auditService.record("PROPERTY_DEACTIVATED", "PROPERTY", property.getId(), "Imóvel " + property.getCode());
    }

    @Transactional
    public PropertyPhotoDto uploadPhoto(UUID propertyId, MultipartFile file) {
        String contentType = file.getContentType();
        validatePhoto(file, contentType);
        RentalProperty property = getEntity(propertyId);
        StoredFile stored = storageService.storeMultipart("photos/properties/" + property.getId(), file);
        PropertyPhoto photo = new PropertyPhoto();
        photo.setProperty(property);
        photo.setFileName(stored.fileName());
        photo.setOriginalFileName(file.getOriginalFilename());
        photo.setContentType(stored.contentType());
        photo.setFilePath(stored.storagePath());
        photo.setSizeBytes(stored.sizeBytes());
        PropertyPhoto saved = photoRepository.save(photo);
        auditService.record("PROPERTY_PHOTO_UPLOADED", "PROPERTY", property.getId(), "Foto vinculada ao imóvel " + property.getCode());
        return PropertyMapper.photoToDto(saved);
    }

    private void validatePhoto(MultipartFile file, String contentType) {
        if (file.isEmpty()) {
            throw new BusinessException("O arquivo enviado está vazio.");
        }
        if (file.getSize() > MAX_PHOTO_SIZE_BYTES) {
            throw new BusinessException("A foto deve ter no máximo 8 MB.");
        }
        String normalizedType = contentType == null ? "" : contentType.toLowerCase(Locale.ROOT);
        if (!ALLOWED_IMAGE_TYPES.contains(normalizedType)) {
            throw new BusinessException("Envie uma imagem JPG, PNG ou WEBP.");
        }
        try {
            if (!"image/webp".equals(normalizedType) && ImageIO.read(file.getInputStream()) == null) {
                throw new BusinessException("O conteúdo do arquivo não parece ser uma imagem válida.");
            }
        } catch (IOException exception) {
            throw new BusinessException("Não foi possível validar a imagem enviada.");
        }
    }
}
