package com.fareye.sphere.d.services;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fareye.sphere.d.dtos.SeatUpdateEventDto;
import com.fareye.sphere.d.entities.enums.Department;

import java.time.LocalDate;

public interface SseService {
    SseEmitter subscribe(Department department, LocalDate date);
    void broadcastSeatUpdate(SeatUpdateEventDto eventDto);
}