package com.fareye.sphere.d.repositories;

import com.fareye.sphere.d.entities.Request;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Page<Request> findByRequestedFor_UserId(Long userId, Pageable pageable);
}
