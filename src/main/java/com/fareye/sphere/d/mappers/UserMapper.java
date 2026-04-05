package com.fareye.sphere.d.mappers;

import com.fareye.sphere.d.dtos.UserDto;
import com.fareye.sphere.d.entities.Asset;
import com.fareye.sphere.d.entities.Booking;
import com.fareye.sphere.d.entities.Request;
import com.fareye.sphere.d.entities.User;
import com.fareye.sphere.d.repositories.AssetRepository;
import com.fareye.sphere.d.repositories.BookingRepository;
import com.fareye.sphere.d.repositories.RequestRepository;
import com.fareye.sphere.d.utils.IdUtils;
import lombok.Setter;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Setter(onMethod_ = {@Autowired})
@Mapper(componentModel = "spring")
public abstract class UserMapper {

    protected IdUtils idUtils;

    protected AssetRepository assetRepository;

    protected RequestRepository requestRepository;

    protected BookingRepository bookingRepository;

    @Mapping(target = "userId", source = "userId", qualifiedByName = "formatUserId")
    @Mapping(target = "requestIds", source = "requests")
    @Mapping(target = "assetSerialNumbers", source = "assets")
    @Mapping(target = "bookingIds", source = "bookings")
    @Mapping(target = "password", ignore = true)
    public abstract UserDto toDto(User entity);

    @Mapping(target = "userId", source = "userId", qualifiedByName = "parseUserId")
    @Mapping(target = "requests", source = "requestIds")
    @Mapping(target = "assets", source = "assetSerialNumbers")
    @Mapping(target = "bookings", source = "bookingIds")
    public abstract User toEntity(UserDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "password", ignore = true)
    public abstract void updateUserFromDto(UserDto dto, @MappingTarget User entity);

    // Helper Methods
    @Named("formatUserId")
    public String formatUserId(Long id) { return idUtils.formatUserId(id).orElse(null); }

    @Named("parseUserId")
    public Long parseUserId(String id) { return idUtils.parseUserId(id).orElse(null); }

    public String assetToId(Asset asset) { return asset != null ? idUtils.formatSerialNumber(asset.getSerialNumber()).orElse(null) : null; }
    public Asset idToAsset(String id) { return idUtils.parseSerialNumber(id).map(assetRepository::getReferenceById).orElse(null); }

    public String requestToId(Request req) { return req != null ? idUtils.formatRequestId(req.getRequestId()).orElse(null) : null; }
    public Request idToRequest(String id) { return idUtils.parseRequestId(id).map(requestRepository::getReferenceById).orElse(null); }

    public String bookingToId(Booking b) { return b != null ? idUtils.formatBookingId(b.getBookingId()).orElse(null) : null; }
    public Booking idToBooking(String id) { return idUtils.parseBookingId(id).map(bookingRepository::getReferenceById).orElse(null); }
}