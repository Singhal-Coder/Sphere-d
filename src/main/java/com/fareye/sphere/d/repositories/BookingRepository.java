package com.fareye.sphere.d.repositories;

import com.fareye.sphere.d.entities.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface BookingRepository extends JpaRepository<Booking, Long> {
     Page<Booking> findByBookedDateAfter(LocalDate date, Pageable pageable);
}