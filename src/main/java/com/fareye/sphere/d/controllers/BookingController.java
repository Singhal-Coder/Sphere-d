package com.fareye.sphere.d.controllers;

import com.fareye.sphere.d.advices.ApiResponse;
import com.fareye.sphere.d.dtos.BookingDto;
import com.fareye.sphere.d.entities.enums.Role;
import com.fareye.sphere.d.services.BookingService;
import com.fareye.sphere.d.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/seats/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final SecurityUtils securityUtils;

    @PostMapping("/{seat-id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<BookingDto>> createBooking(@PathVariable("seat-id") String seatId, @Valid @RequestBody BookingDto bookingDto) {
        bookingDto.setSeatId(seatId);
        bookingDto.setUserId(securityUtils.getCurrentUserId());
        BookingDto createdBooking = bookingService.createBooking(bookingDto);
        ApiResponse<BookingDto> response = new ApiResponse<>(HttpStatus.CREATED.value(), "Seat booked successfully", createdBooking);
        
        response.add(linkTo(methodOn(BookingController.class).getBookingById(createdBooking.getBookingId())).withSelfRel());
        response.add(linkTo(methodOn(BookingController.class).cancelBooking(createdBooking.getBookingId())).withRel("cancel-booking"));
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM') or @authz.isBookingOwner(#id)")
    public ResponseEntity<ApiResponse<BookingDto>> getBookingById(@PathVariable String id) {
        BookingDto booking = bookingService.getBookingById(id);
        ApiResponse<BookingDto> response = new ApiResponse<>(HttpStatus.OK.value(), "Booking fetched", booking);
        
        Role role = securityUtils.getCurrentUserRole();
        String currentUserId = securityUtils.getCurrentUserId();

        response.add(linkTo(methodOn(BookingController.class).getBookingById(id)).withSelfRel());
        
        if (role == Role.SYSTEM || currentUserId.equals(booking.getUserId())) {
            response.add(linkTo(methodOn(BookingController.class).cancelBooking(id)).withRel("cancel-booking"));
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SYSTEM','EMPLOYEE')")
    public ResponseEntity<ApiResponse<Page<BookingDto>>> getAllBookings(
            Pageable pageable,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Role role = securityUtils.getCurrentUserRole();
        Page<BookingDto> bookings = role == Role.EMPLOYEE
                ? bookingService.getAllBookingsAfterDateByDepartment(
                        pageable, date, securityUtils.getCurrentUserDepartment())
                : bookingService.getAllBookingsAfterDate(pageable, date);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Bookings fetched", bookings));
    }

    @PatchMapping("/{booking-id}/cancel") // URL matrix mein seat-id tha but functionally booking-id hona chahiye cancel ke liye
    @PreAuthorize("hasRole('SYSTEM') or @authz.isBookingOwner(#bookingId)")
    public ResponseEntity<ApiResponse<BookingDto>> cancelBooking(@PathVariable("booking-id") String bookingId) {
        BookingDto cancelledBooking = bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Booking cancelled", cancelledBooking));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('SYSTEM')")
    public ResponseEntity<ApiResponse<Void>> deleteBookings(
            @RequestParam(required = false) String bookingId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tillDate,
            @RequestParam(required = false) String seatId) {
        
        // Is method ko service mein implement karna padega (System job ke liye)
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.NO_CONTENT.value(), "Bookings deleted by system", null));
    }
}