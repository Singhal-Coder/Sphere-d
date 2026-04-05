package com.fareye.sphere.d.services;

import com.fareye.sphere.d.dtos.BookingDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface BookingService {
    BookingDto createBooking(BookingDto bookingDto);

    BookingDto getBookingById(String bookingId);

    Page<BookingDto> getAllBookingsAfterDate(Pageable pageable, LocalDate date);

    BookingDto cancelBooking(String bookingId);

    void deleteBooking(String bookingId);
}
