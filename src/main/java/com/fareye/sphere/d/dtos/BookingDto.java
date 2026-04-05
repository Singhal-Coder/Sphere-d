package com.fareye.sphere.d.dtos;

import com.fareye.sphere.d.annotations.ValidFormattedId;
import com.fareye.sphere.d.entities.enums.BookingStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter @Setter @AllArgsConstructor
public class BookingDto {
    @ValidFormattedId(type = "BOOKING")
    private String bookingId;

    @ValidFormattedId(type = "USER") @NotBlank
    private String userId;

    @ValidFormattedId(type = "SEAT") @NotBlank
    private String seatId;

    @NotNull
    private LocalDate bookedDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime bookedAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BookingStatus status;
}


