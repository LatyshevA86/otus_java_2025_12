package ru.otus.model;

import java.util.List;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;
import ru.otus.dto.AddressDto;
import ru.otus.dto.ClientDto;
import ru.otus.dto.PhoneDto;

@Table("client")
public record Client(
        @Id Long id,
        String name,
        @MappedCollection(idColumn = "client_id") Address address,
        @MappedCollection(idColumn = "client_id") Set<Phone> phones) {

    public ClientDto toDto() {
        AddressDto addressDto =
                this.address() != null ? new AddressDto(this.address().street()) : null;
        List<PhoneDto> phones = this.phones() != null
                ? this.phones().stream().map(p -> new PhoneDto(p.number())).toList()
                : List.of();
        return new ClientDto(this.name(), addressDto, phones);
    }
}
