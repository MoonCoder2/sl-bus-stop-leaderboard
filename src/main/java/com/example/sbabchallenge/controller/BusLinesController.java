package com.example.sbabchallenge.controller;

import com.example.sbabchallenge.model.BusLineDTO;
import com.example.sbabchallenge.service.BusLinesService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/bus-lines")
public class BusLinesController {
    private final BusLinesService busLinesService;

    public BusLinesController(BusLinesService busLinesService) {
        this.busLinesService = busLinesService;
    }

    @Tag(description = "Return the SL bus lines with most bus stops counting both direction", name = "Bus Lines With Most Bus Stops")
    @GetMapping("/most-bus-stops")
    public ResponseEntity<List<BusLineDTO>> getBusLinesWithMostBusStops(@RequestParam(defaultValue = "10") int numberOfRowsToReturn){
        return ResponseEntity.ok()
                .cacheControl( CacheControl.maxAge(30, TimeUnit.SECONDS)
                        .noTransform()
                        .mustRevalidate())
                .body(busLinesService.getBusLinesWithMostBusStops(numberOfRowsToReturn));
    }
}
