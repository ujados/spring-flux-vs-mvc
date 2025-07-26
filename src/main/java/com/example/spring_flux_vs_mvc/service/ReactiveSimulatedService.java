package com.example.spring_flux_vs_mvc.service;

import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;


@Service
@Slf4j
public class ReactiveSimulatedService {
    public Mono<String> simulateNonBlockingOperation() {
        // Simula una operación I/O no bloqueante
        return Mono
                .delay(Duration.ofMillis(100))
                .doOnSubscribe(sub -> log.info("simulateNonBlockingOperation started"))
                .map(i -> "result");
    }
}