package com.example.spring_flux_vs_mvc.controller;

import org.springframework.web.bind.annotation.GetMapping;
import com.example.spring_flux_vs_mvc.service.BlockingSimulatedService;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class BlockingController {

    private final BlockingSimulatedService service;

    public BlockingController(BlockingSimulatedService service) {
        this.service = service;
    }

    @GetMapping("/blocking")
    public String getBlocking() {
        return service.simulateBlockingOperation();
    }
}
