package com.fareye.sphere.d.controllers;

import com.fareye.sphere.d.entities.enums.Department;
import com.fareye.sphere.d.services.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") 
public class SseController {

    private final SseService sseService;

    // Endpoint format: GET /api/sse/seats?department=IT&date=2026-04-10
    @GetMapping(value = "/seats", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeToSeats(
            @RequestParam Department department,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        return sseService.subscribe(department.name(), date);
    }
}