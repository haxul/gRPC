package grpc.greeting.clients;

import com.proto.calculator.*;
import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class GreetingClient {

    public void run() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
        errorSquareHandling(channel);
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

    @SneakyThrows
    public void getAverage(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceStub asyncClient = CalculatorServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<AverageRequest> requestStreamObserver = asyncClient.getAverage(new StreamObserver<AverageResponse>() {
            @Override
            public void onNext(AverageResponse value) {
                System.out.println("Average is " + value.getAverage());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        requestStreamObserver.onNext(AverageRequest.newBuilder().setNumber(0).build());
        requestStreamObserver.onNext(AverageRequest.newBuilder().setNumber(55).build());
        requestStreamObserver.onNext(AverageRequest.newBuilder().setNumber(5).build());
        requestStreamObserver.onNext(AverageRequest.newBuilder().setNumber(40).build());
        requestStreamObserver.onCompleted();
        latch.await();
    }

    @SneakyThrows
    public void doBidirectionalCall(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<GreetEveryOneRequest> greetEveryOneRequestStreamObserver = asyncClient.greetEveryOne(new StreamObserver<GreetEveryOneResponse>() {

            @Override
            public void onNext(GreetEveryOneResponse value) {
                System.out.println("Got from server " + value.getResult());
            }

            @Override
            public void onError(Throwable t) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        Arrays.asList("Person1", "Person2", "Person3").forEach( person -> {
            greetEveryOneRequestStreamObserver.onNext(GreetEveryOneRequest.newBuilder().setName(person).build());
        });
        greetEveryOneRequestStreamObserver.onCompleted();
        latch.await();
    }

    public void errorSquareHandling(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub client = CalculatorServiceGrpc.newBlockingStub(channel);
        SquareRootRequest request = SquareRootRequest.newBuilder().setNumber(-1).build();
        try {
            SquareRootResponse squareRootResponse = client.squareRoot(request);
            System.out.println(squareRootResponse.getNumber());
        } catch (StatusRuntimeException exception) {
            System.out.println(exception.getStatus().getCode());
        }

    }

    public static void main(String[] args) {
        System.out.println("Start  Greeting Client");
        var client = new GreetingClient();
        client.run();

    }
}
