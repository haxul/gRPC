package grpc.greeting.server;

import com.proto.calculator.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;

public class CalculatorServiceImp extends CalculatorServiceGrpc.CalculatorServiceImplBase {

    @Override
    public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {
        int firstNumber = request.getFirstNumber();
        int secondNumber = request.getSecondNumber();
        SumResponse response = SumResponse.newBuilder().setResult(firstNumber + secondNumber).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<AverageRequest> getAverage(StreamObserver<AverageResponse> responseObserver) {
        return new StreamObserver<>() {

            private List<Integer> numbers = new ArrayList<>(10);

            @Override
            public void onNext(AverageRequest value) {
                numbers.add(value.getNumber());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                int sum = 0;
                for (var num : numbers) {
                    sum += num;
                }
                int average = sum / numbers.size();
                AverageResponse averageResponse = AverageResponse.newBuilder().setAverage(average).build();
                responseObserver.onNext(averageResponse);
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void squareRoot(SquareRootRequest request, StreamObserver<SquareRootResponse> responseObserver) {
        double number = request.getNumber();
        if (number < 0) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Negative number").asRuntimeException());
            return;
        }
        double result = Math.sqrt(number);
        SquareRootResponse squareRootResponse = SquareRootResponse.newBuilder().setNumber(result).build();
        responseObserver.onNext(squareRootResponse);
        responseObserver.onCompleted();
    }
}
