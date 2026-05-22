package br.com.ultimoandar.contracts.mapper;

import br.com.ultimoandar.contracts.dto.PropertyDto;
import br.com.ultimoandar.contracts.dto.PropertyPhotoDto;
import br.com.ultimoandar.contracts.entity.PropertyPhoto;
import br.com.ultimoandar.contracts.entity.RentalProperty;
import java.util.List;

public final class PropertyMapper {

    private PropertyMapper() {
    }

    public static PropertyDto toDto(RentalProperty property) {
        List<PropertyPhotoDto> photos = property.getPhotos() == null
                ? List.of()
                : property.getPhotos().stream().map(PropertyMapper::photoToDto).toList();
        return new PropertyDto(
                property.getId(),
                property.getCode(),
                property.getType(),
                AddressMapper.toDto(property.getAddress()),
                property.getDescription(),
                property.getMonthlyRent(),
                property.getCondominiumFee(),
                property.getIptuValue(),
                property.getStatus(),
                property.getOwner().getId(),
                property.getOwner().getName(),
                property.getInternalNotes(),
                photos,
                property.getCreatedAt(),
                property.getUpdatedAt()
        );
    }

    public static void copyToEntity(PropertyDto dto, RentalProperty property) {
        property.setCode(dto.code());
        property.setType(dto.type());
        property.setAddress(AddressMapper.toEntity(dto.address()));
        property.setDescription(dto.description());
        property.setMonthlyRent(dto.monthlyRent());
        property.setCondominiumFee(dto.condominiumFee());
        property.setIptuValue(dto.iptuValue());
        property.setStatus(dto.status());
        property.setInternalNotes(dto.internalNotes());
    }

    public static PropertyPhotoDto photoToDto(PropertyPhoto photo) {
        return new PropertyPhotoDto(
                photo.getId(),
                photo.getFileName(),
                photo.getOriginalFileName(),
                photo.getContentType(),
                photo.getSizeBytes(),
                photo.getCreatedAt()
        );
    }
}
