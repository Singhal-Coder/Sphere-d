package com.fareye.sphere.d.services.impl;

import com.fareye.sphere.d.dtos.BookingDto;
import com.fareye.sphere.d.dtos.SeatUpdateEventDto;
import com.fareye.sphere.d.entities.Booking;
import com.fareye.sphere.d.entities.Seat;
import com.fareye.sphere.d.entities.User;
import com.fareye.sphere.d.entities.enums.BookingStatus;
import com.fareye.sphere.d.entities.enums.SeatStatus;
import com.fareye.sphere.d.exceptions.BusinessPolicyException;
import com.fareye.sphere.d.exceptions.InvalidIdException;
import com.fareye.sphere.d.exceptions.InvalidWorkflowStateException;
import com.fareye.sphere.d.exceptions.ResourceNotFoundException;
import com.fareye.sphere.d.mappers.BookingMapper;
import com.fareye.sphere.d.repositories.BookingRepository;
import com.fareye.sphere.d.repositories.SeatRepository;
import com.fareye.sphere.d.repositories.UserRepository;
import com.fareye.sphere.d.services.BookingService;
import com.fareye.sphere.d.services.SseService;
import com.fareye.sphere.d.utils.IdUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
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
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;

    private final SseService sseService;

    @Override
    public BookingDto createBooking(BookingDto bookingDto) {
        User user = userRepository.findById(idUtils.parseUserId(bookingDto.getUserId()).get())
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", bookingDto.getUserId()));
        
        Seat seat = seatRepository.findById(idUtils.parseSeatId(bookingDto.getSeatId()).get())
            .orElseThrow(() -> new ResourceNotFoundException("Seat", "id", bookingDto.getSeatId()));

        // 2. CHECK: Department Match
        if (user.getDepartment() != seat.getDepartment()) {
                throw new BusinessPolicyException("You can only book seats in your own department: " + user.getDepartment());
        }
        
        try {
                Booking booking = bookingMapper.toEntity(bookingDto);
                Booking savedBooking = bookingRepository.save(booking);
                
                SeatUpdateEventDto eventDto = SeatUpdateEventDto.builder()
                    .seatId(bookingDto.getSeatId())
                    .date(booking.getBookedDate())
                    .status(SeatStatus.UNAVAILABLE)
                    .department(seat.getDepartment())
                    .build();
                
                sseService.broadcastSeatUpdate(eventDto);

                return bookingMapper.toDto(savedBooking);
        } catch (DataIntegrityViolationException ex) {
                throw new BusinessPolicyException("Booking failed: Either the seat is already booked, or you have reached your 1-seat-per-day limit.");
        }
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

        if (booking.getStatus() == BookingStatus.CANCELLED) {
                throw new InvalidWorkflowStateException("Booking is already cancelled.");
        }

        if (booking.getBookedDate().isBefore(LocalDate.now())) {
                throw new BusinessPolicyException("Cannot cancel past bookings.");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking updatedBooing = bookingRepository.save(booking);

        SeatUpdateEventDto eventDto = SeatUpdateEventDto.builder()
                    .seatId(idUtils.formatSeatId(booking.getSeat().getSeatId()).get())
                    .date(booking.getBookedDate())
                    .status(SeatStatus.AVAILABLE)
                    .department(booking.getSeat().getDepartment())
                    .build();
                
        sseService.broadcastSeatUpdate(eventDto);

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
