package ru.otus.protobuf.service;

import io.grpc.stub.StreamObserver;
import ru.otus.protobuf.NumberMessage;
import ru.otus.protobuf.NumberSequenceMessage;
import ru.otus.protobuf.RemoteNumberGeneratorServiceGrpc;

public class RemoteNumberGeneratorServiceImpl
        extends RemoteNumberGeneratorServiceGrpc.RemoteNumberGeneratorServiceImplBase {

    private final NumberGeneratorService numberGeneratorService;

    public RemoteNumberGeneratorServiceImpl(NumberGeneratorService numberGeneratorService) {
        this.numberGeneratorService = numberGeneratorService;
    }

    @Override
    public void generate(NumberSequenceMessage request, StreamObserver<NumberMessage> responseObserver) {
        numberGeneratorService
                .generate(request.getFirstValue(), request.getLastValue())
                .forEach(n -> {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        System.err.println(e.getMessage());
                    }
                    responseObserver.onNext(
                            NumberMessage.newBuilder().setNewValue(n).build());
                });
        responseObserver.onCompleted();
    }
}
