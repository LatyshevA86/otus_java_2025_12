package ru.otus.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.otus.dto.ClientDto;
import ru.otus.model.Address;
import ru.otus.model.Client;
import ru.otus.model.Phone;
import ru.otus.repository.ClientRepository;

@Controller
public class ClientController {

    private final ClientRepository clientRepository;

    public ClientController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @GetMapping("/")
    public String clientsPage() {
        return "clients";
    }

    @PostMapping("/api/clients")
    @ResponseBody
    public ClientDto createClient(@RequestBody ClientDto dto) {
        Address address = dto.address() != null ? new Address(dto.address().street()) : null;
        Set<Phone> phones = dto.phones() != null
                ? dto.phones().stream().map(p -> new Phone(p.number())).collect(Collectors.toSet())
                : Set.of();
        Client client = new Client(null, dto.name(), address, phones);
        clientRepository.save(client);
        return client.toDto();
    }

    @GetMapping("/api/clients")
    @ResponseBody
    public List<ClientDto> getClients() {
        return clientRepository.findAll().stream().map(Client::toDto).toList();
    }
}
