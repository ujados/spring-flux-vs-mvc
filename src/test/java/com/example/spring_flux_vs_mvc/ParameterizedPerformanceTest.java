package com.example.spring_flux_vs_mvc;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.stream.Stream;

import static com.example.spring_flux_vs_mvc.util.LoadTestUtil.runLoadTest;
import static com.example.spring_flux_vs_mvc.util.LoadTestUtil.runAsyncLoadTest;

public class ParameterizedPerformanceTest {

    private static final String BASE_URL = "http://localhost:8080";
    private final RestTemplate restTemplate = new RestTemplate();
    private final WebClient webClient = WebClient.create(BASE_URL);

    enum TestType {
        BLOCKING,
        REACTIVE
    }

    record TestScenario(String label, int totalRequests, int threads, TestType type) {}


    static Stream<TestScenario> testScenarios() {
        return Stream.of(
                new TestScenario("BLOCKING - 10req - 2threads", 10, 2, TestType.BLOCKING),
                new TestScenario("BLOCKING - 100req - 10threads", 100, 10, TestType.BLOCKING),
                new TestScenario("BLOCKING - 500req - 20threads", 500, 20, TestType.BLOCKING),

                new TestScenario("REACTIVE - 10req - 2threads", 10, 2, TestType.REACTIVE),
                new TestScenario("REACTIVE - 100req - 10threads", 100, 10, TestType.REACTIVE),
                new TestScenario("REACTIVE - 500req - 20threads", 500, 20, TestType.REACTIVE)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("testScenarios")
    public void runPerformanceTest(TestScenario scenario) throws InterruptedException {
        if (scenario.type == TestType.BLOCKING) {
            runLoadTest(scenario.label, scenario.totalRequests, scenario.threads,
                    () -> restTemplate.getForObject(BASE_URL + "/blocking", String.class));
        } else {
            runAsyncLoadTest(scenario.label, scenario.totalRequests, scenario.threads,
                    () -> webClient.get().uri("/reactive")
                            .retrieve()
                            .bodyToMono(String.class));
        }
    }
}