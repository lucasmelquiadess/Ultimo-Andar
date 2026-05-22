package br.com.ultimoandar.contracts.mapper;

import br.com.ultimoandar.contracts.dto.AddressDto;
import br.com.ultimoandar.contracts.entity.Address;
import br.com.ultimoandar.contracts.exception.BusinessException;

public final class AddressMapper {

    private AddressMapper() {
    }

    public static Address toEntity(AddressDto dto) {
        Address address = new Address();
        if (dto == null) {
            return address;
        }
        address.setPostalCode(normalizePostalCode(dto.postalCode()));
        address.setStreet(dto.street());
        address.setNumber(dto.number());
        address.setComplement(dto.complement());
        address.setNeighborhood(dto.neighborhood());
        address.setCity(dto.city());
        address.setState(dto.state());
        address.setCountry(dto.country() == null || dto.country().isBlank() ? "Brasil" : dto.country());
        return address;
    }

    public static AddressDto toDto(Address address) {
        if (address == null) {
            return null;
        }
        return new AddressDto(
                address.getPostalCode(),
                address.getStreet(),
                address.getNumber(),
                address.getComplement(),
                address.getNeighborhood(),
                address.getCity(),
                address.getState(),
                address.getCountry()
        );
    }

    private static String normalizePostalCode(String postalCode) {
        if (postalCode == null || postalCode.isBlank()) {
            return null;
        }
        String digits = postalCode.replaceAll("\\D", "");
        if (digits.length() != 8) {
            throw new BusinessException("Informe um CEP válido com 8 dígitos.");
        }
        return digits.substring(0, 5) + "-" + digits.substring(5);
    }
}
