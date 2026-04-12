package ru.otus.servlet;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.dto.ClientDto;

@SuppressWarnings({"java:S1989"})
public class ClientsApiServlet extends HttpServlet {

    private final transient DBServiceClient dbServiceClient;
    private final transient Gson gson;

    public ClientsApiServlet(DBServiceClient dbServiceClient, Gson gson) {
        this.dbServiceClient = dbServiceClient;
        this.gson = gson;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<ClientDto> clients = dbServiceClient.findAll().stream()
                .map(client -> new ClientDto(
                        client.getName(),
                        client.getAddress() != null
                                ? new ClientDto.AddressDto(client.getAddress().getStreet())
                                : null,
                        client.getPhones() != null
                                ? client.getPhones().stream()
                                        .map(phone -> new ClientDto.PhoneDto(phone.getNumber()))
                                        .toList()
                                : List.of()))
                .toList();
        response.setContentType("application/json;charset=UTF-8");
        response.getOutputStream().print(gson.toJson(clients));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var dto = gson.fromJson(request.getReader(), ClientDto.class);

        Address address =
                dto.address() != null ? new Address(null, dto.address().street()) : null;

        List<Phone> phones = new ArrayList<>();
        if (dto.phones() != null) {
            dto.phones().forEach(p -> phones.add(new Phone(null, p.number())));
        }

        dbServiceClient.saveClient(new Client(null, dto.name(), address, phones));

        response.setStatus(HttpServletResponse.SC_OK);
    }
}
