package com.fareye.sphere.d.services;

import com.fareye.sphere.d.dtos.BookingDto;
import com.fareye.sphere.d.entities.enums.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface BookingService {
    BookingDto createBooking(BookingDto bookingDto);

    BookingDto getBookingById(String bookingId);

    Page<BookingDto> getAllBookingsAfterDate(Pageable pageable, LocalDate date);

    Page<BookingDto> getAllBookingsAfterDateByDepartment(
            Pageable pageable,
            LocalDate date,
            Department department);

    BookingDto cancelBooking(String bookingId);

    void deleteBooking(String bookingId);
}
