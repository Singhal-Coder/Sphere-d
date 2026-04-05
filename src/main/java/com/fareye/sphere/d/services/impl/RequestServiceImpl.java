package com.fareye.sphere.d.services.impl;

import com.fareye.sphere.d.dtos.RequestDto;
import com.fareye.sphere.d.entities.Request;
import com.fareye.sphere.d.entities.RequestLog;
import com.fareye.sphere.d.entities.enums.Role;
import com.fareye.sphere.d.exceptions.InvalidIdException;
import com.fareye.sphere.d.exceptions.ResourceNotFoundException;
import com.fareye.sphere.d.mappers.RequestMapper;
import com.fareye.sphere.d.repositories.RequestLogRepository;
import com.fareye.sphere.d.repositories.RequestRepository;
import com.fareye.sphere.d.repositories.UserRepository;
import com.fareye.sphere.d.services.RequestService;
import com.fareye.sphere.d.utils.IdUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final RequestLogRepository requestLogRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;
    private final IdUtils idUtils;

    private void updateLogs(Request request){
        RequestLog requestLog = RequestLog.builder()
                .status(request.getStatus())
                .time(LocalDateTime.now())
                .modifier(request.getLastModifier())
                .parentRequest(request)
                .build();

        requestLogRepository.save(requestLog);
    }

    @Override
    public RequestDto createRequest(RequestDto requestDto) {
        Request request = requestMapper.toEntity(requestDto);
        if (requestDto.getLastModifierRole()== Role.SYSTEM){
            request.setLastModifier(
                    userRepository.findByRole(Role.SYSTEM).getFirst()
            );
        }else if (requestDto.getLastModifierRole()== Role.EMPLOYEE){
            request.setLastModifier(
                    request.getRequestedFor()
            );
        }
        Request savedRequest = requestRepository.save(request);
        updateLogs(savedRequest);
        return requestMapper.toDto(savedRequest);
    }

    @Override
    public RequestDto getRequestById(String requestId) {
        Long id = idUtils.parseRequestId(requestId)
                .orElseThrow(() -> new InvalidIdException(requestId));

        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request", "id", requestId));

        return requestMapper.toDto(request);
    }

    @Override
    public RequestDto updateRequest(String requestId, RequestDto requestDto) {
        Long id = idUtils.parseRequestId(requestId)
                .orElseThrow(() -> new InvalidIdException(requestId));

        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request", "id", requestId));

        requestMapper.updateRequestFromDto(requestDto, request);

        Request updatedRequest = requestRepository.save(request);

        updateLogs(updatedRequest);

        return requestMapper.toDto(updatedRequest);
    }

    @Override
    public void deleteRequest(String requestId) {
        Long id = idUtils.parseRequestId(requestId)
                .orElseThrow(() -> new InvalidIdException(requestId));

        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request", "id", requestId));

        requestRepository.delete(request);
    }
}
