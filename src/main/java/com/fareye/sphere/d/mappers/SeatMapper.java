package com.fareye.sphere.d.mappers;

import com.fareye.sphere.d.dtos.SeatDto;
import com.fareye.sphere.d.entities.Booking;
import com.fareye.sphere.d.entities.Seat;
import com.fareye.sphere.d.utils.BookingDateLimitUtils;
import com.fareye.sphere.d.utils.IdUtils;
import lombok.Setter;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Setter(onMethod_ = {@Autowired})
@Mapper(componentModel = "spring")
public abstract class SeatMapper {

    protected IdUtils idUtils;
    protected BookingDateLimitUtils bookingDateLimitUtils;

    // Entity to DTO
    @Mapping(target = "seatId", source = "seatId", qualifiedByName = "formatSeatId")
    @Mapping(target = "bookingDates", source = "bookings", qualifiedByName = "mapValidBookingDates")
    public abstract SeatDto toDto(Seat entity);

    // DTO to Entity
    @Mapping(target = "seatId", source = "seatId", qualifiedByName = "parseSeatId")
    @Mapping(target = "bookings", ignore = true)
    public abstract Seat toEntity(SeatDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "seatId", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    public abstract void updateSeatFromDto(SeatDto dto, @MappingTarget Seat entity);

    // --- Formatting Helpers ---
    @Named("formatSeatId")
    public String formatSeatId(Long id) { return idUtils.formatSeatId(id).orElse(null); }

    @Named("parseSeatId")
    public Long parseSeatId(String id) {
        return idUtils.parseSeatId(id).orElse(null);
    }

    // --- Collection Helpers ---
    private LocalDate bookingToDate(Booking booking) {
        return booking != null && bookingDateLimitUtils.isDateAllowed(booking.getBookedDate())? booking.getBookedDate():null;
    }

    @Named("mapValidBookingDates")
    public List<LocalDate> mapValidBookingDates(List<Booking> bookings) {
        if (bookings == null) return null;

        return bookings.stream()
                .map(this::bookingToDate)
                .filter(Objects::nonNull)
                .toList();
    }
}