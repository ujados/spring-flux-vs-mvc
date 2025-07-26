package com.example.spring_flux_vs_mvc;

import org.junit.jupiter.api.Test;

public class SequentialPerformanceTest {


    private final PerformanceTest performanceTest = new PerformanceTest();

    @Test
    public void runAllBlockingSequentially() {
        try {
            System.out.println("=== Running Blocking Tests Sequentially ===");
//            performanceTest.testBlocking_10req_2threads();
//            performanceTest.testBlocking_100req_10threads();
//            performanceTest.testBlocking_500req_20threads();
            performanceTest.testBlocking_500req_20threads();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void runAllReactiveSequentially() {
        try {
            System.out.println("=== Running Reactive Tests Sequentially ===");
//            performanceTest.testReactive_10req_2threads();
//            performanceTest.testReactive_100req_10threads();
//            performanceTest.testReactiveAsync_500req_20threads();
            performanceTest.testReactiveAsync_10000req_100threads();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void runAllTestsSequentially() {
        runAllBlockingSequentially();
        runAllReactiveSequentially();
    }
}
