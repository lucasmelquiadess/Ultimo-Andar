package br.com.ultimoandar.contracts.dto;

public record AddressDto(
        String postalCode,
        String street,
        String number,
        String complement,
        String neighborhood,
        String city,
        String state,
        String country
) {
}
