package grpc.greeting.server;

import com.proto.greet.*;
import io.grpc.stub.StreamObserver;
import lombok.SneakyThrows;

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

    @Override
    @SneakyThrows
    public void greetManyTimes(GreetManyTimesRequest request, StreamObserver<GreetManyTimesResponse> responseObserver) {
        var firstName = request.getGreeting().getFirstName();
        for (int i = 0; i < 10; i++) {
            var result = "Hello " + firstName + " " + i;
            var response = GreetManyTimesResponse.newBuilder().setResult(result).build();
            responseObserver.onNext(response);
            Thread.sleep(1000);
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<LongGreetRequest> longGreet(StreamObserver<LongGreetResponse> responseObserver) {
        return new StreamObserver<LongGreetRequest>() {
            private String result = "";

            @Override
            public void onNext(LongGreetRequest value) {
                result += "hello " + value.getGreeting().getFirstName() + "; ";
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                LongGreetResponse response = LongGreetResponse.newBuilder().setResult(result).build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        };
    }
}


