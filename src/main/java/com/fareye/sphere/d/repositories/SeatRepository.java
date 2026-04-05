package com.fareye.sphere.d.repositories;

import com.fareye.sphere.d.entities.Seat;
import com.fareye.sphere.d.entities.enums.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByDepartment(Department department);
    boolean existsByGridXAndGridYAndDepartment(int gridX, int gridY, Department department);
}