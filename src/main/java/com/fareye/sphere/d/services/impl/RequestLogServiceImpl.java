package com.fareye.sphere.d.services.impl;

import com.fareye.sphere.d.dtos.RequestLogDto;
import com.fareye.sphere.d.entities.RequestLog;
import com.fareye.sphere.d.exceptions.InvalidIdException;
import com.fareye.sphere.d.exceptions.ResourceNotFoundException;
import com.fareye.sphere.d.mappers.RequestLogMapper;
import com.fareye.sphere.d.repositories.RequestLogRepository;
import com.fareye.sphere.d.services.RequestLogService;
import com.fareye.sphere.d.utils.IdUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestLogServiceImpl implements RequestLogService {

    private final RequestLogRepository requestLogRepository;
    private final RequestLogMapper requestLogMapper;
    private final IdUtils idUtils;

    @Override
    public RequestLogDto getRequestLogById(String requestLogId) {
        Long id = idUtils.parseRequestLogId(requestLogId)
                .orElseThrow(() -> new InvalidIdException(requestLogId));

        RequestLog requestLog = requestLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RequestLog", "id", requestLogId));

        return requestLogMapper.toDto(requestLog);
    }



    @Override
    public void deleteRequestLog(String requestLogId) {
        Long id = idUtils.parseRequestLogId(requestLogId)
                .orElseThrow(() -> new InvalidIdException(requestLogId));

        RequestLog requestLog = requestLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RequestLog", "id", requestLogId));

        requestLogRepository.delete(requestLog);
    }
}