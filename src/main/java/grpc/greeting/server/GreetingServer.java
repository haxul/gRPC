package grpc.greeting.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.SneakyThrows;

public class GreetingServer {

    @SneakyThrows
    public static void main(String[] args) {
        System.out.println("start greeting server");

        Server server = ServerBuilder
                .forPort(50051)
                .addService(new GreetServiceImp())
                .build();

        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Got shutdown request");
            server.shutdown();
            System.out.println("Successfully stopped the server");
        }));
        server.awaitTermination();
    }
}
