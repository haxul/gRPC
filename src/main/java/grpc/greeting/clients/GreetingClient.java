package grpc.greeting.clients;

import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.SumRequest;
import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.SneakyThrows;

import java.util.concurrent.CountDownLatch;

public class GreetingClient {

    public void run() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
        doClientStreamCall(channel);
        channel.shutdown();
    }

    public void doStreamServerCall(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceBlockingStub syncGreetClient = GreetServiceGrpc.newBlockingStub(channel);
        Greeting greeting = Greeting.newBuilder().setFirstName("haxul").setLastName("some").build();
        GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest.newBuilder().setGreeting(greeting).build();
        syncGreetClient.greetManyTimes(greetManyTimesRequest).forEachRemaining(result -> {
            System.out.println(result.getResult());
        });
    }

    public void doUnaryCall(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceBlockingStub syncGreetClient = GreetServiceGrpc.newBlockingStub(channel);
        Greeting greeting = Greeting.newBuilder().setFirstName("haxul").setLastName("something").build();
        GreetRequest greetRequest = GreetRequest.newBuilder().setGreeting(greeting).build();
        GreetResponse greet = syncGreetClient.greet(greetRequest);
        System.out.println(greet.getResult());
    }

    @SneakyThrows
    public void doClientStreamCall(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<LongGreetRequest> longGreetRequestStreamObserver = asyncClient.longGreet(new StreamObserver<>() {
            @Override
            public void onNext(LongGreetResponse value) {
                System.out.println("Got response from server");
                System.out.println(value.getResult());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                System.out.println("Server send to client");
                latch.countDown();
            }
        });

        longGreetRequestStreamObserver.onNext(
                LongGreetRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("haxul").build()).build()
        );

        longGreetRequestStreamObserver.onNext(
                LongGreetRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("guy").build()).build()
        );

        longGreetRequestStreamObserver.onNext(
                LongGreetRequest.newBuilder().setGreeting(Greeting.newBuilder().setFirstName("another guy").build()).build()
        );

        longGreetRequestStreamObserver.onCompleted();
        latch.await();

    }

    public static void main(String[] args) {
        System.out.println("Start  Greeting Client");
        var client = new GreetingClient();
        client.run();

    }
}
