package com.fareye.sphere.d.services;

import com.fareye.sphere.d.dtos.SeatDto;
import com.fareye.sphere.d.entities.enums.Department;

import java.util.List;

public interface SeatService {
    SeatDto createSeat(SeatDto seatDto);

    SeatDto getSeatById(String seatId);

    List<SeatDto> getSeatByDepartment(Department department);

    SeatDto updateSeat(String seatId, SeatDto seatDto);

    void deleteSeat(String seatId);
}
