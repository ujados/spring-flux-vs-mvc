package com.example.spring_flux_vs_mvc.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import com.example.spring_flux_vs_mvc.service.ReactiveSimulatedService;

@RestController
public class ReactiveController {

    private final ReactiveSimulatedService service;

    public ReactiveController(ReactiveSimulatedService service) {
        this.service = service;
    }

    @GetMapping("/reactive")
    public Mono<String> getReactive() {
        return service.simulateNonBlockingOperation();
    }
}

