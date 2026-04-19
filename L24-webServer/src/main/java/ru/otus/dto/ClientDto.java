package ru.otus.dto;

import java.util.List;

public record ClientDto(String name, AddressDto address, List<PhoneDto> phones) {
    public record AddressDto(String street) {}

    public record PhoneDto(String number) {}
}
