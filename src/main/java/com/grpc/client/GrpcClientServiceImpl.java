package com.grpc.client;

import com.example.grpc.server.grpcserver.ResourceRequest;
import com.example.grpc.server.grpcserver.ResourceResponse;
import com.example.grpc.server.grpcserver.ResourceServiceGrpc;
import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GrpcClientServiceImpl implements GrpcClientService {

    private final ResourceServiceGrpc.ResourceServiceFutureStub futureStub;
    private final ResourceServiceGrpc.ResourceServiceBlockingStub blockingStub;
    private final ResourceServiceGrpc.ResourceServiceStub asyncStub;

    @Autowired
    public GrpcClientServiceImpl(@Value("${server.host}") String host, @Value("${grpc.server.port}") int port) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();

        blockingStub = ResourceServiceGrpc.newBlockingStub(channel);
        asyncStub = ResourceServiceGrpc.newStub(channel);
        futureStub = ResourceServiceGrpc.newFutureStub(channel);
    }

    public ResourceResponse ping(String resourceUrl) {
        if(resourceUrl == null) throw new IllegalArgumentException("resourceUrl is null");

        return blockingStub.ping(ResourceRequest.newBuilder()
                .setResourceUrl(resourceUrl)
                .build());
    }

    public ListenableFuture<ResourceResponse> getFuturePing(String resourceUrl) {
        if(resourceUrl == null) throw new IllegalArgumentException("resourceUrl is null");

        ResourceRequest resourceRequest = ResourceRequest.newBuilder().setResourceUrl(resourceUrl).build();
        return futureStub.ping(resourceRequest);
    }

    public void pingAsync(String resourceUrl) {
        if(resourceUrl == null) throw new IllegalArgumentException("resourceUrl is null");

        ResourceRequest resourceRequest = ResourceRequest.newBuilder().setResourceUrl(resourceUrl).build();

        StreamObserver<ResourceResponse> streamObserver =  new StreamObserver<ResourceResponse>() {
            @Override
            public void onNext(ResourceResponse resourceResponse) {
                log.info("pingAsync :: resource = '{}' , responseCode = {} , time = {}", resourceResponse.getResourceUrl(), resourceResponse.getResponseCode(), resourceResponse.getTime());
            }

            @Override
            public void onError(Throwable throwable) {
                log.info("pingAsync error =  {}", throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                log.info("pingAsync :: completed");
            }
        };
        asyncStub.ping(resourceRequest, streamObserver);
    }
}
