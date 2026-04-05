package com.fareye.sphere.d.mappers;

import com.fareye.sphere.d.dtos.BookingDto;
import com.fareye.sphere.d.entities.Booking;
import com.fareye.sphere.d.entities.Seat;
import com.fareye.sphere.d.entities.User;
import com.fareye.sphere.d.repositories.SeatRepository;
import com.fareye.sphere.d.repositories.UserRepository;
import com.fareye.sphere.d.utils.IdUtils;
import lombok.Setter;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
@Setter(onMethod_ = {@Autowired})
public abstract class BookingMapper {

    protected IdUtils idUtils;

    protected UserRepository userRepository;

    protected SeatRepository seatRepository;

    @Mapping(target = "bookingId", source = "bookingId", qualifiedByName = "formatBookingId")
    @Mapping(target = "userId", source = "user.userId", qualifiedByName = "formatUserId")
    @Mapping(target = "seatId", source = "seat.seatId", qualifiedByName = "formatSeatId")
    public abstract BookingDto toDto(Booking entity);

    @Mapping(target = "bookingId", source = "bookingId", qualifiedByName = "parseBookingId")
    @Mapping(target = "user", source = "userId")
    @Mapping(target = "seat", source = "seatId")
    public abstract Booking toEntity(BookingDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "bookingId", ignore = true)
    public abstract void updateBookingFromDto(BookingDto dto, @MappingTarget Booking entity);

    @Named("formatBookingId")
    public String formatBookingId(Long id) { return idUtils.formatBookingId(id).orElse(null); }
    @Named("parseBookingId")
    public Long parseBookingId(String id) { return idUtils.parseBookingId(id).orElse(null); }

    @Named("formatUserId")
    public String formatUserId(Long id) { return idUtils.formatUserId(id).orElse(null); }
    @Named("formatSeatId")
    public String formatSeatId(Long id) { return idUtils.formatSeatId(id).orElse(null); }

    public User idToUser(String id) { return idUtils.parseUserId(id).map(userRepository::getReferenceById).orElse(null); }
    public Seat idToSeat(String id) { return idUtils.parseSeatId(id).map(seatRepository::getReferenceById).orElse(null); }
}