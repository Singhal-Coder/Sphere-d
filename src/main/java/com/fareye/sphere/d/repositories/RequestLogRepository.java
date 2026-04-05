package com.fareye.sphere.d.repositories;

import com.fareye.sphere.d.entities.RequestLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestLogRepository extends JpaRepository<RequestLog, Long> {
}
