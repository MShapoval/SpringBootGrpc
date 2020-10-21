package com.grpc.server;

import com.example.grpc.server.grpcserver.ResourceRequest;
import com.example.grpc.server.grpcserver.ResourceResponse;
import com.example.grpc.server.grpcserver.ResourceServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

@GrpcService
@Slf4j
public class GrpcServerServiceImpl extends ResourceServiceGrpc.ResourceServiceImplBase {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void ping(ResourceRequest request, StreamObserver<ResourceResponse> responseObserver) {
        long startTime = System.currentTimeMillis();
        int statusCode = 0;

        try {
            ResponseEntity<String> httpResponse = restTemplate.exchange(
                    request.getResourceUrl(),
                    HttpMethod.GET,
                    new HttpEntity<>(new HttpHeaders()),
                    String.class);

            statusCode = httpResponse.getStatusCodeValue();
        } catch (HttpStatusCodeException | UnknownHttpStatusCodeException e) {
            log.error("ping :: ex {}", e.getResponseBodyAsString());
            statusCode = e.getRawStatusCode();
        }

        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);

        ResourceResponse response = ResourceResponse.newBuilder()
                .setTime(duration)
                .setResponseCode(statusCode)
                .setResourceUrl(request.getResourceUrl())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
