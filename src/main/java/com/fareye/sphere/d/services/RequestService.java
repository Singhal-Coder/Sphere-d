package com.fareye.sphere.d.services;

import com.fareye.sphere.d.dtos.RequestDto;
import com.fareye.sphere.d.entities.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RequestService {
    RequestDto createRequest(RequestDto requestDto);

    Page<RequestDto> getAllRequests(Pageable pageable, Role role, Long currentUserId);

    RequestDto getRequestById(String requestId);

    RequestDto updateRequest(String requestId, RequestDto requestDto);

    void deleteRequest(String requestId);
}
