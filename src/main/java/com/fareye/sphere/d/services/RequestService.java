package com.fareye.sphere.d.services;

import com.fareye.sphere.d.dtos.RequestDto;

public interface RequestService {
    RequestDto createRequest(RequestDto requestDto);

    RequestDto getRequestById(String requestId);

    RequestDto updateRequest(String requestId, RequestDto requestDto);

    void deleteRequest(String requestId);
}
