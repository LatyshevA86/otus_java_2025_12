package ru.otus.protobuf;

import io.grpc.ServerBuilder;
import java.io.IOException;
import ru.otus.protobuf.service.NumberGeneratorServiceImpl;
import ru.otus.protobuf.service.RemoteNumberGeneratorServiceImpl;

@SuppressWarnings({"squid:S106"})
public class GRPCServer {

    public static final int SERVER_PORT = 8190;

    public static void main(String[] args) throws IOException, InterruptedException {

        var numberGeneratorService = new NumberGeneratorServiceImpl();
        var remoteNumberGeneratorService = new RemoteNumberGeneratorServiceImpl(numberGeneratorService);

        var server = ServerBuilder.forPort(SERVER_PORT)
                .addService(remoteNumberGeneratorService)
                .build();
        server.start();
        System.out.println("server waiting for client connections...");
        server.awaitTermination();
    }
}
