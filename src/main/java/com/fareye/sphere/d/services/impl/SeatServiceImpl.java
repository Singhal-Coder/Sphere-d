package com.fareye.sphere.d.services.impl;

import com.fareye.sphere.d.dtos.SeatDto;
import com.fareye.sphere.d.entities.Seat;
import com.fareye.sphere.d.entities.enums.BookingStatus;
import com.fareye.sphere.d.entities.enums.Department;
import com.fareye.sphere.d.exceptions.DuplicateResourceException;
import com.fareye.sphere.d.exceptions.InvalidIdException;
import com.fareye.sphere.d.exceptions.ResourceInUseException;
import com.fareye.sphere.d.exceptions.ResourceNotFoundException;
import com.fareye.sphere.d.mappers.SeatMapper;
import com.fareye.sphere.d.repositories.SeatRepository;
import com.fareye.sphere.d.services.SeatService;
import com.fareye.sphere.d.utils.IdUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final SeatMapper seatMapper;
    private final IdUtils idUtils;

    @Override
    public SeatDto createSeat(SeatDto seatDto) {
        // CHECK: Ensure no duplicate seat exists at the same Grid X, Y within the department
        boolean exists = seatRepository.existsByGridXAndGridYAndDepartment(
                seatDto.getGridX(), seatDto.getGridY(), seatDto.getDepartment());
        
        if (exists) {
            throw new DuplicateResourceException("Seat", "Grid coordinates", 
                    "X:" + seatDto.getGridX() + ", Y:" + seatDto.getGridY() + " in " + seatDto.getDepartment());
        }

        Seat seat = seatMapper.toEntity(seatDto);
        Seat savedSeat = seatRepository.save(seat);
        return seatMapper.toDto(savedSeat);
    }

    @Override
    public SeatDto getSeatById(String seatId) {
        Long id = idUtils.parseSeatId(seatId)
                .orElseThrow(() -> new InvalidIdException(seatId));

        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seat", "id", seatId));

        return seatMapper.toDto(seat);
    }

    @Override
    public List<SeatDto> getSeatByDepartment(Department department){
        return seatRepository.findByDepartment(department).stream()
                .map(seatMapper::toDto)
                .toList();
    }

    @Override
    public SeatDto updateSeat(String seatId, SeatDto seatDto) {
        Long id = idUtils.parseSeatId(seatId)
                .orElseThrow(() -> new InvalidIdException(seatId));

        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seat", "id", seatId));

        seatMapper.updateSeatFromDto(seatDto, seat);
        Seat updatedSeat = seatRepository.save(seat);

        return seatMapper.toDto(updatedSeat);
    }

    @Override
    public void deleteSeat(String seatId) {
        Long id = idUtils.parseSeatId(seatId)
                .orElseThrow(() -> new InvalidIdException(seatId));

        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seat", "id", seatId));

        // CHECK: Cannot delete seat if it has active future bookings
        boolean hasFutureBookings = seat.getBookings() != null && seat.getBookings().stream()
                .anyMatch(booking -> booking.getStatus() == BookingStatus.ACTIVE && !booking.getBookedDate().isBefore(LocalDate.now()));
        
        if (hasFutureBookings) {
            throw new ResourceInUseException("Cannot delete seat because it has active future bookings.");
        }

        seatRepository.delete(seat);
    }
}