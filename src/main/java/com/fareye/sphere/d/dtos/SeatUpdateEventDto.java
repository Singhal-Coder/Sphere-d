package com.fareye.sphere.d.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import com.fareye.sphere.d.entities.enums.Department;
import com.fareye.sphere.d.entities.enums.SeatStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeatUpdateEventDto {
    private String seatId;
    private LocalDate date;
    private SeatStatus status;
    private Department department;
}