package com.fareye.sphere.d.entities;

import com.fareye.sphere.d.annotations.FutureBookingLimit;
import com.fareye.sphere.d.annotations.ValidSeatAllocation;
import com.fareye.sphere.d.entities.enums.BookingStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor @Getter @Setter @AllArgsConstructor
@Entity
@Table(name = "bookings")
@ValidSeatAllocation
public class Booking {
    @Id
    @SequenceGenerator(name = "booking_id_seq",sequenceName = "booking_id_seq",allocationSize = 1)
    @GeneratedValue(generator = "booking_id_seq", strategy = GenerationType.SEQUENCE)
    private Long bookingId;

    @ManyToOne
    @JoinColumn(name = "booked_by") @NotNull
    private User user;

    @ManyToOne
    @JoinColumn(name = "seat_no") @NotNull
    private Seat seat;

    @FutureBookingLimit @NotNull
    private LocalDate bookedDate;

    @CreationTimestamp
    private LocalDateTime bookedAt;

    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.ACTIVE;
}