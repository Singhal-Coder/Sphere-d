package com.fareye.sphere.d.dtos;

import com.fareye.sphere.d.annotations.ValidFormattedId;
import com.fareye.sphere.d.entities.enums.Department;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter @AllArgsConstructor
public class SeatDto {
    @ValidFormattedId(type = "SEAT")
    private String seatId;

    @Min(0)
    private int gridX;

    @Min(0)
    private int gridY;
    private Department department;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<LocalDate> bookingDates;
}