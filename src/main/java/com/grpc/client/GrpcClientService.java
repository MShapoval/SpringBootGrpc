package com.grpc.client;

import com.example.grpc.server.grpcserver.ResourceResponse;
import com.google.common.util.concurrent.ListenableFuture;

public interface GrpcClientService {
    ResourceResponse ping(String resourceUrl);
    ListenableFuture<ResourceResponse> getFuturePing(String resourceUrl);
    void pingAsync(String resourceUrl);
}
