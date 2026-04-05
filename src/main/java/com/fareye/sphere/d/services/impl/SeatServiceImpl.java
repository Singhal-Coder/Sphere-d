package com.fareye.sphere.d.services.impl;

import com.fareye.sphere.d.dtos.SeatDto;
import com.fareye.sphere.d.entities.Seat;
import com.fareye.sphere.d.entities.enums.Department;
import com.fareye.sphere.d.exceptions.InvalidIdException;
import com.fareye.sphere.d.exceptions.ResourceNotFoundException;
import com.fareye.sphere.d.mappers.SeatMapper;
import com.fareye.sphere.d.repositories.SeatRepository;
import com.fareye.sphere.d.services.SeatService;
import com.fareye.sphere.d.utils.IdUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final SeatMapper seatMapper;
    private final IdUtils idUtils;

    @Override
    public SeatDto createSeat(SeatDto seatDto) {
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

        seatRepository.delete(seat);
    }

}
