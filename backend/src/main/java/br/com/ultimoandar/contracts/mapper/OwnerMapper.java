package br.com.ultimoandar.contracts.mapper;

import br.com.ultimoandar.contracts.dto.OwnerDto;
import br.com.ultimoandar.contracts.entity.Owner;

public final class OwnerMapper {

    private OwnerMapper() {
    }

    public static OwnerDto toDto(Owner owner) {
        return new OwnerDto(
                owner.getId(),
                owner.getPersonType(),
                owner.getName(),
                owner.getDocument(),
                owner.getIdentityNumber(),
                owner.getNationality(),
                owner.getMaritalStatus(),
                owner.getProfession(),
                AddressMapper.toDto(owner.getAddress()),
                owner.getPhone(),
                owner.getEmail(),
                owner.getBankDetails(),
                owner.getNotes(),
                owner.isActive(),
                owner.getCreatedAt(),
                owner.getUpdatedAt()
        );
    }

    public static void copyToEntity(OwnerDto dto, Owner owner) {
        owner.setPersonType(dto.personType());
        owner.setName(dto.name());
        owner.setDocument(dto.document());
        owner.setIdentityNumber(dto.identityNumber());
        owner.setNationality(dto.nationality());
        owner.setMaritalStatus(dto.maritalStatus());
        owner.setProfession(dto.profession());
        owner.setAddress(AddressMapper.toEntity(dto.address()));
        owner.setPhone(dto.phone());
        owner.setEmail(dto.email());
        owner.setBankDetails(dto.bankDetails());
        owner.setNotes(dto.notes());
        owner.setActive(dto.active() == null || dto.active());
    }
}
