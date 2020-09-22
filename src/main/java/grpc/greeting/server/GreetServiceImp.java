package grpc.greeting.server;

import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.stub.StreamObserver;

public class GreetServiceImp extends GreetServiceGrpc.GreetServiceImplBase {

    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        Greeting greeting = request.getGreeting();
        var firstName = greeting.getFirstName();
        var response = "Hello " + firstName;
        GreetResponse greetResponse = GreetResponse.newBuilder().setResult(response).build();
        responseObserver.onNext(greetResponse);
        responseObserver.onCompleted();
    }
}
