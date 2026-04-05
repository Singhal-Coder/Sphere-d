package com.fareye.sphere.d.services;

import com.fareye.sphere.d.dtos.RequestLogDto;

public interface RequestLogService {

    RequestLogDto getRequestLogById(String requestLogId);

    void deleteRequestLog(String requestLogId);
}
