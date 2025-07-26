package com.example.spring_flux_vs_mvc.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BlockingSimulatedService {
    public String simulateBlockingOperation() {
        try {
            Thread.sleep(100); // Simula operación I/O bloqueante
            log.info("simulateBlockingOperation started");
        } catch (InterruptedException ignored) {}
        return "result";
    }
}