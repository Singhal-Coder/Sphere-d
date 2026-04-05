package com.fareye.sphere.d.services.impl;

import com.fareye.sphere.d.dtos.SeatUpdateEventDto;
import com.fareye.sphere.d.entities.enums.Department;
import com.fareye.sphere.d.services.SseService;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseServiceImpl implements SseService {
    private final Map<String, List<SseEmitter>> emittersMap = new ConcurrentHashMap<>();

    private String buildKey(Department department, LocalDate date) {
        return department + "_" + date.toString();
    }

    @Override
    public SseEmitter subscribe(Department department, LocalDate date) {
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L); // 30 mins timeout
        String key = buildKey(department, date);
        
        emittersMap.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(key, emitter));
        emitter.onTimeout(() -> removeEmitter(key, emitter));
        emitter.onError((e) -> removeEmitter(key, emitter));

        try {
            emitter.send(
                SseEmitter.event()
                .name("init")
                .data("Connected to " + key + " SSE stream")
            );
        } catch (IOException e) {
            removeEmitter(key, emitter);
        }

        return emitter;
    }

    @Override
    public void broadcastSeatUpdate(SeatUpdateEventDto eventDto) {
        String key = buildKey(eventDto.getDepartment(), eventDto.getDate());
        List<SseEmitter> targetEmitters = emittersMap.get(key);
        
        if (targetEmitters != null) {
            
            for (SseEmitter emitter : targetEmitters) {
                try {
                    emitter.send(
                        SseEmitter.event()
                        .name("seat-update")
                        .data(eventDto, MediaType.APPLICATION_JSON)
                    );
                } catch (IOException e) {
                    removeEmitter(key, emitter);
                }
            }
        }
    }

    private void removeEmitter(String key, SseEmitter emitter) {
        List<SseEmitter> targetEmitters = emittersMap.get(key);
        if (targetEmitters != null) {
            targetEmitters.remove(emitter);
            if (targetEmitters.isEmpty()) {
                emittersMap.remove(key);
            }
        }
    }
}