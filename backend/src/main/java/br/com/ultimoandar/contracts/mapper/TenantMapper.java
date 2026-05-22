package br.com.ultimoandar.contracts.mapper;

import br.com.ultimoandar.contracts.dto.TenantDto;
import br.com.ultimoandar.contracts.entity.Tenant;

public final class TenantMapper {

    private TenantMapper() {
    }

    public static TenantDto toDto(Tenant tenant) {
        return new TenantDto(
                tenant.getId(),
                tenant.getPersonType(),
                tenant.getName(),
                tenant.getDocument(),
                tenant.getIdentityNumber(),
                tenant.getNationality(),
                tenant.getMaritalStatus(),
                tenant.getProfession(),
                AddressMapper.toDto(tenant.getAddress()),
                tenant.getPhone(),
                tenant.getEmail(),
                tenant.getSpouseData(),
                tenant.getGuarantorData(),
                tenant.getNotes(),
                tenant.isActive(),
                tenant.getCreatedAt(),
                tenant.getUpdatedAt()
        );
    }

    public static void copyToEntity(TenantDto dto, Tenant tenant) {
        tenant.setPersonType(dto.personType());
        tenant.setName(dto.name());
        tenant.setDocument(dto.document());
        tenant.setIdentityNumber(dto.identityNumber());
        tenant.setNationality(dto.nationality());
        tenant.setMaritalStatus(dto.maritalStatus());
        tenant.setProfession(dto.profession());
        tenant.setAddress(AddressMapper.toEntity(dto.address()));
        tenant.setPhone(dto.phone());
        tenant.setEmail(dto.email());
        tenant.setSpouseData(dto.spouseData());
        tenant.setGuarantorData(dto.guarantorData());
        tenant.setNotes(dto.notes());
        tenant.setActive(dto.active() == null || dto.active());
    }
}
