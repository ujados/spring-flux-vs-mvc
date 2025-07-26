package com.example.spring_flux_vs_mvc;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

public class PerformanceTest {

    private static final String BASE_URL = "http://localhost:8080";
    private static final int TOTAL_REQUESTS = 10;
    private static final int THREADS = 100;

    @Test
    public void blockingLoadTest() throws Exception {
        System.out.println("Running BLOCKING test...");
        RestTemplate restTemplate = new RestTemplate();
        runLoadTest(TOTAL_REQUESTS, THREADS, i -> {
            restTemplate.getForObject(BASE_URL + "/blocking", String.class);
        });
    }

    @Test
    public void reactiveUsesLoadTest() throws Exception {
        System.out.println("Running REACTIVE test...");
        WebClient client = WebClient.create(BASE_URL);
        runLoadTest(TOTAL_REQUESTS, THREADS, i -> {
            Mono<String> response = client.get()
                    .uri("/reactive")
                    .retrieve()
                    .bodyToMono(String.class);
        });
    }

    @Test
    public void reactiveBadUsesLoadTest() throws Exception {
        System.out.println("Running REACTIVE test...");
        WebClient client = WebClient.create(BASE_URL);
        runLoadTest(TOTAL_REQUESTS, THREADS, i -> {
            Mono<String> response = client.get()
                    .uri("/reactive")
                    .retrieve()
                    .bodyToMono(String.class);
            response.block(); // para forzar ejecución síncrona
        });
    }

    private void runLoadTest(int totalRequests, int threads, ThrowingConsumer<Integer> task) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        List<Callable<Void>> callables = IntStream.range(0, totalRequests)
                .mapToObj(i -> (Callable<Void>) () -> {
                    task.accept(i);
                    return null;
                }).toList();

        Instant start = Instant.now();
        List<Future<Void>> results = executor.invokeAll(callables);
        Instant end = Instant.now();

        long completed = results.stream().filter(f -> {
            try {
                f.get();
                return true;
            } catch (Exception e) {
                System.err.println("Request failed: " + e.getCause());
                return false;
            }
        }).count();

        long totalTimeMs = Duration.between(start, end).toMillis();
        System.out.println("Completed: " + completed + "/" + totalRequests);
        System.out.println("Total time: " + totalTimeMs + " ms");
        System.out.println("Avg per request: " + (totalTimeMs / (double) completed) + " ms");
        System.out.println("Throughput: " + (1000.0 * completed / totalTimeMs) + " req/s");

        executor.shutdown();
    }

    @FunctionalInterface
    interface ThrowingConsumer<T> {
        void accept(T t) throws Exception;
    }
}
