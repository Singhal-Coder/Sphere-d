package com.fareye.sphere.d.services.impl;

import com.fareye.sphere.d.dtos.BookingDto;
import com.fareye.sphere.d.entities.Booking;
import com.fareye.sphere.d.entities.enums.BookingStatus;
import com.fareye.sphere.d.exceptions.InvalidIdException;
import com.fareye.sphere.d.exceptions.ResourceNotFoundException;
import com.fareye.sphere.d.mappers.BookingMapper;
import com.fareye.sphere.d.repositories.BookingRepository;
import com.fareye.sphere.d.services.BookingService;
import com.fareye.sphere.d.utils.IdUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final IdUtils idUtils;

    @Override
    public BookingDto createBooking(BookingDto bookingDto) {
        Booking booking = bookingMapper.toEntity(bookingDto);
        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(savedBooking);
    }

    @Override
    public BookingDto getBookingById(String bookingId) {
        Long id = idUtils.parseBookingId(bookingId)
                .orElseThrow(() -> new InvalidIdException(bookingId));

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));

        return bookingMapper.toDto(booking);
    }

    @Override
    public Page<BookingDto> getAllBookingsAfterDate(Pageable pageable, LocalDate date){
        return bookingRepository.findByBookedDateAfter(date, pageable)
                .map(bookingMapper::toDto);
    }

    @Override
    public BookingDto cancelBooking(String bookingId){
        Long id = idUtils.parseBookingId(bookingId)
                .orElseThrow(() -> new InvalidIdException(bookingId));

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));

        booking.setStatus(BookingStatus.CANCELLED);
        Booking updatedBooing = bookingRepository.save(booking);
        return bookingMapper.toDto(updatedBooing);
    }

    @Override
    public void deleteBooking(String bookingId) {
        Long id = idUtils.parseBookingId(bookingId)
                .orElseThrow(() -> new InvalidIdException(bookingId));

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));

        bookingRepository.delete(booking);
    }
}
