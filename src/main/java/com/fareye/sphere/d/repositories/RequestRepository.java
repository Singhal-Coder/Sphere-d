package com.fareye.sphere.d.repositories;

import com.fareye.sphere.d.entities.Request;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<Request, Long> {
}
