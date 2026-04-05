package com.fareye.sphere.d.controllers;

import com.fareye.sphere.d.advices.ApiResponse;
import com.fareye.sphere.d.dtos.SeatDto;
import com.fareye.sphere.d.entities.enums.Department;
import com.fareye.sphere.d.entities.enums.Role;
import com.fareye.sphere.d.services.SeatService;
import com.fareye.sphere.d.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;
    private final SecurityUtils securityUtils;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SYSTEM')")
    public ResponseEntity<ApiResponse<SeatDto>> createSeat(@RequestBody @Valid SeatDto seatDto){
        SeatDto createdSeat = seatService.createSeat(seatDto);
        ApiResponse<SeatDto> response = new ApiResponse<>(HttpStatus.CREATED.value(), "Seat created successfully", createdSeat);
        response.add(linkTo(methodOn(SeatController.class).getSeatById(createdSeat.getSeatId())).withSelfRel());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SeatDto>>> getSeatsByDepartment(@RequestParam Department department) {
        List<SeatDto> seats = seatService.getSeatByDepartment(department);
        ApiResponse<List<SeatDto>> response = new ApiResponse<>(HttpStatus.OK.value(), "Seats fetched for department: " + department, seats);
        
        response.add(linkTo(methodOn(SeatController.class).getSeatsByDepartment(department)).withSelfRel());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SeatDto>> getSeatById(@PathVariable String id) {
        SeatDto seat = seatService.getSeatById(id);
        ApiResponse<SeatDto> response = new ApiResponse<>(HttpStatus.OK.value(), "Seat fetched", seat);
        
        Role role = securityUtils.getCurrentUserRole();

        response.add(linkTo(methodOn(SeatController.class).getSeatById(id)).withSelfRel());

        if (role == Role.ADMIN || role == Role.SYSTEM) {
            response.add(linkTo(methodOn(SeatController.class).deleteSeat(id)).withRel("delete-seat"));
        }

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SYSTEM')")
    public ResponseEntity<ApiResponse<Void>> deleteSeat(@PathVariable String id) {
        seatService.deleteSeat(id);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.NO_CONTENT.value(), "Seat deleted", null));
    }
}