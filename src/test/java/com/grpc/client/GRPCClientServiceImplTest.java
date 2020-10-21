package com.grpc.client;

import com.example.grpc.server.grpcserver.ResourceResponse;
import com.google.common.util.concurrent.ListenableFuture;
import com.grpc.Application;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = Application.class)
@Slf4j
class GRPCClientServiceImplTest {

    @Autowired
    private GrpcClientService grpcClientService;

    private static final List<String> webResourcesList;

    static {
        webResourcesList = new ArrayList<>();
        webResourcesList.add("https://www.google.com/");
        webResourcesList.add("https://www.facebook.com/");
        webResourcesList.add("https://youtube.com/");
        webResourcesList.add("https://microsoft.com/");
        webResourcesList.add("https://instagram.com/");
        webResourcesList.add("https://netflix.com/");
        webResourcesList.add("https://wikipedia.org/");
        webResourcesList.add("https://amazon.in/");
        webResourcesList.add("https://apple.com/");
        webResourcesList.add("https://adobe.com/");
        webResourcesList.add("https://twitter.com/");
        webResourcesList.add("https://stackoverflow.com");
        webResourcesList.add("https://habr.com/");
        webResourcesList.add("https://spring.io/");
        webResourcesList.add("https://docs.spring.io/");
        webResourcesList.add("https://junit.org/");
        webResourcesList.add("https://www.baeldung.com/");
        webResourcesList.add("https://devcolibri.com/");
        webResourcesList.add("https://github.com/");
        webResourcesList.add("https://www.oracle.com/");
    }

    @Test
    void multiThreadBlockingCallTest() throws InterruptedException, ExecutionException {
        List<CompletableFuture<ResourceResponse>> completableFutureList = new ArrayList<>();
        webResourcesList.forEach(resource -> completableFutureList.add(CompletableFuture.supplyAsync(() -> grpcClientService.ping(resource))));

        for (CompletableFuture<ResourceResponse> future : completableFutureList) {
            ResourceResponse resourceResponse = future.get();
            log.info("multiThreadBlockingCallTest :: resource = '{}' , responseCode = {} , time : {}", resourceResponse.getResourceUrl(), resourceResponse.getResponseCode(), resourceResponse.getTime());
            assertEquals(resourceResponse.getResponseCode(), 200);
        }
    }

    @Test
    void multiThreadNonBlockingCallTest() throws InterruptedException, ExecutionException{
        List<ListenableFuture<ResourceResponse>> listenableFutures = new ArrayList<>();
        webResourcesList.forEach(resource -> listenableFutures.add(grpcClientService.getFuturePing(resource)));

        for (ListenableFuture<ResourceResponse> future : listenableFutures) {
            ResourceResponse resourceResponse = future.get();
            log.info("multiThreadNonBlockingCallTest :: resource = '{}' , responseCode = {} , time : {}", resourceResponse.getResourceUrl(), resourceResponse.getResponseCode(), resourceResponse.getTime());
            assertEquals(resourceResponse.getResponseCode(), 200);
        }
    }

    @Test
    void singleTreadBlockingCallTest() {
        for (String s : webResourcesList) {
            ResourceResponse resourceResponse = grpcClientService.ping(s);
            log.info("singleTreadBlockingCallTest :: resource = '{}' , responseCode = {} , time = {}", resourceResponse.getResourceUrl(), resourceResponse.getResponseCode(), resourceResponse.getTime());
            assertEquals(resourceResponse.getResponseCode(), 200);
        }
    }

    @Test
    void singleTreadNonBlockingCallTest() throws InterruptedException{
        for (String s : webResourcesList) {
            grpcClientService.pingAsync(s);
        }
        Thread.sleep(6000); // waiting for callbacks
    }

}
