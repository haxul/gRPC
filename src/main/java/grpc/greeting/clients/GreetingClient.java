package grpc.greeting.clients;

import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {
    public static void main(String[] args) {
        System.out.println("Start  Greeting Client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        GreetServiceGrpc.GreetServiceBlockingStub syncGreetClient = GreetServiceGrpc.newBlockingStub(channel);
        Greeting greeting = Greeting.newBuilder().setFirstName("haxul").setLastName("something").build();
        GreetRequest greetRequest = GreetRequest.newBuilder().setGreeting(greeting).build();
        GreetResponse greet = syncGreetClient.greet(greetRequest);
        System.out.println(greet.getResult());
        System.out.println("Channel is shutdown");
        channel.shutdown();

    }
}
