package grpc.greeting.clients;

import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.SumRequest;
import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;

public class GreetingClient {
    public static void main(String[] args) {
        System.out.println("Start  Greeting Client");
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();


        GreetServiceGrpc.GreetServiceBlockingStub syncGreetClient = GreetServiceGrpc.newBlockingStub(channel);
/*
      === Unary ====
        Greeting greeting = Greeting.newBuilder().setFirstName("haxul").setLastName("something").build();
        GreetRequest greetRequest = GreetRequest.newBuilder().setGreeting(greeting).build();
        GreetResponse greet = syncGreetClient.greet(greetRequest);
        System.out.println(greet.getResult());

        CalculatorServiceGrpc.CalculatorServiceBlockingStub calculatorClient = CalculatorServiceGrpc.newBlockingStub(channel);
        int result = calculatorClient.sum(SumRequest.newBuilder().setFirstNumber(100).setSecondNumber(200).build()).getResult();
        System.out.println(result);
 */
        // Server Streaming ====================
        Greeting greeting = Greeting.newBuilder().setFirstName("haxul").setLastName("some").build();
        GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest.newBuilder().setGreeting(greeting).build();
        syncGreetClient.greetManyTimes(greetManyTimesRequest).forEachRemaining(result -> {
            System.out.println(result.getResult());
        });
        channel.shutdown();
    }
}
