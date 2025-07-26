package com.example.spring_flux_vs_mvc;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.CountDownLatch;

import static com.example.spring_flux_vs_mvc.util.LoadTestUtil.runAsyncLoadTest;
import static com.example.spring_flux_vs_mvc.util.LoadTestUtil.runLoadTest;

public class PerformanceTest {

    private static final String BASE_URL = "http://localhost:8080";
    private final RestTemplate restTemplate = new RestTemplate();
    private final WebClient webClient = WebClient.create(BASE_URL);

// --- BLOCKING TESTS ---

    @Test
    public void testBlocking_10req_2threads() {
        runLoadTest("BLOCKING - 10req - 2threads", 10, 2,
                () -> restTemplate.getForObject(BASE_URL + "/blocking", String.class));
    }

    @Test
    public void testBlocking_100req_10threads() {
        runLoadTest("BLOCKING - 100req - 10threads", 100, 10,
                () -> restTemplate.getForObject(BASE_URL + "/blocking", String.class));
    }

    @Test
    public void testBlocking_500req_20threads() {
        runLoadTest("BLOCKING - 500req - 20threads", 500, 20,
                () -> restTemplate.getForObject(BASE_URL + "/blocking", String.class));
    }

    @Test
    public void testBlocking_10000req_100threads() {
        runLoadTest("BLOCKING - 500req - 20threads", 10000, 100,
                () -> restTemplate.getForObject(BASE_URL + "/blocking", String.class));
    }

    // --- REACTIVE TESTS ---

    @Test
    public void testReactive_10req_2threads() throws InterruptedException {
        runAsyncLoadTest("REACTIVE - 10req - 2threads", 10, 2,
                () -> webClient.get().uri("/reactive")
                        .retrieve()
                        .bodyToMono(String.class));
    }

    @Test
    public void testReactive_100req_10threads() throws InterruptedException {
        runAsyncLoadTest("REACTIVE - 100req - 10threads", 100, 10,
                () -> webClient.get().uri("/reactive")
                        .retrieve()
                        .bodyToMono(String.class));
    }

    @Test
    public void testReactiveAsync_500req_20threads() throws InterruptedException {
        runAsyncLoadTest("REACTIVE ASYNC - 500req - 20threads", 500, 20,
                () -> webClient.get()
                        .uri("/reactive")
                        .retrieve()
                        .bodyToMono(String.class));
    }

    @Test
    public void testReactiveAsync_10000req_100threads() throws InterruptedException {
        runAsyncLoadTest("REACTIVE ASYNC - 500req - 20threads", 10000, 100,
                () -> webClient.get()
                        .uri("/reactive")
                        .retrieve()
                        .bodyToMono(String.class));
    }

    @Test
    public void reactiveBadUsesLoadTest() {
        runLoadTest("REACTIVE - 500req - 20threads", 500, 20,
                () -> webClient.get().uri("/reactive")
                        .retrieve()
                        .bodyToMono(String.class)
                        .block());
    }

}
