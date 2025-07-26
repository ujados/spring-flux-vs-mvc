package com.example.spring_flux_vs_mvc.util;

import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class LoadTestUtil {
    public record LoadTestResult(
            String label,
            long completed,
            int totalRequests,
            long totalTimeMs,
            double avgPerRequestMs,
            double throughputReqPerSec
    ) {}

    public static LoadTestResult runLoadTest(String label, int totalRequests, int threads, Supplier<String> requestFn) {
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        long start = System.nanoTime();

        List<Future<String>> results = new CopyOnWriteArrayList<>();

        for (int i = 0; i < totalRequests; i++) {
            results.add(executor.submit(requestFn::get));
        }

        long completed = results.stream().filter(f -> {
            try {
                f.get();
                return true;
            } catch (Exception e) {
                System.err.println("Request failed: " + e.getCause());
                return false;
            }
        }).count();

        executor.shutdown();
        long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        double avgPerRequestMs = completed > 0 ? (durationMs / (double) completed) : Double.POSITIVE_INFINITY;
        double throughputReqPerSec = completed > 0 ? (1000.0 * completed / durationMs) : 0.0;

        System.out.println("Label: " + label);
        System.out.println("Completed: " + completed + "/" + totalRequests);
        System.out.println("Total time: " + durationMs + " ms");
        System.out.println("Avg per request: " + avgPerRequestMs + " ms");
        System.out.println("Throughput: " + throughputReqPerSec + " req/s");

        return new LoadTestResult(
                label,
                completed,
                totalRequests,
                durationMs,
                avgPerRequestMs,
                throughputReqPerSec
        );
    }


    public static LoadTestResult runAsyncLoadTest(String label, int totalRequests, int threads, Supplier<Mono<String>> requestFn) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(totalRequests);
        List<Long> latencies = Collections.synchronizedList(new CopyOnWriteArrayList<>());

        Instant start = Instant.now();

        for (int i = 0; i < totalRequests; i++) {
            long requestStart = System.nanoTime();

            requestFn.get()
                    .doOnNext(response -> {
                        long elapsedNs = System.nanoTime() - requestStart;
                        latencies.add(elapsedNs);
                    })
                    .doOnError(err -> System.err.println("Request error: " + err.getMessage()))
                    .doFinally(signal -> latch.countDown())
                    .subscribe();
        }

        latch.await();

        long totalTimeMs = Duration.between(start, Instant.now()).toMillis();
        long completed = latencies.size();
        long durationMs = (long) (1000.0 * completed / totalTimeMs);
        double avgPerRequestMs = completed > 0 ? (durationMs / (double) completed) : Double.POSITIVE_INFINITY;
        double throughputReqPerSec = completed > 0 ? (1000.0 * completed / durationMs) : 0.0;

        System.out.println("Label: " + label);
        System.out.println("Completed: " + completed + "/" + totalRequests);
        System.out.println("Total time: " + durationMs + " ms");
        System.out.println("Avg per request: " + avgPerRequestMs + " ms");
        System.out.println("Throughput: " + throughputReqPerSec + " req/s");

        return new LoadTestResult(
                label,
                completed,
                totalRequests,
                durationMs,
                avgPerRequestMs,
                throughputReqPerSec
        );
    }


    private static void runLoadTest(int totalRequests, int threads, ThrowingConsumer<Integer> task) throws InterruptedException {
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
