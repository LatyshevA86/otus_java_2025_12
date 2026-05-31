package ru.otus.protobuf;

import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

@SuppressWarnings({"squid:S106", "squid:S2142"})
public class GRPCClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8190;
    private static final long FIRST_VALUE = 0L;
    private static final long LAST_VALUE = 30L;
    private static long currentValue = FIRST_VALUE;
    private static long lastValueFromServer;
    private static boolean isNewValue = false;

    public static void main(String[] args) throws InterruptedException {
        var channel = ManagedChannelBuilder.forAddress(SERVER_HOST, SERVER_PORT)
                .usePlaintext()
                .build();

        var stub = RemoteNumberGeneratorServiceGrpc.newStub(channel);
        var numberMessage = NumberSequenceMessage.newBuilder()
                .setFirstValue(FIRST_VALUE)
                .setLastValue(LAST_VALUE)
                .build();

        stub.generate(numberMessage, new StreamObserver<>() {
            @Override
            public void onNext(NumberMessage value) {
                lastValueFromServer = value.getNewValue();
                System.out.println("new value: " + lastValueFromServer);
                isNewValue = true;
            }

            @Override
            public void onError(Throwable t) {
                System.err.println(t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("request completed");
            }
        });

        for (int i = 0; i < 50; i++) {
            Thread.sleep(1000);
            currentValue = currentValue + 1;
            if (isNewValue) {
                currentValue += lastValueFromServer;
                isNewValue = false;
            }
            System.out.println("current value: " + currentValue);
        }

        channel.shutdown();
    }
}
