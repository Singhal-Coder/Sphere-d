package com.fareye.sphere.d.controllers;

import com.fareye.sphere.d.advices.ApiResponse;
import com.fareye.sphere.d.dtos.RequestDto;
import com.fareye.sphere.d.entities.enums.Role;
import com.fareye.sphere.d.services.RequestService;
import com.fareye.sphere.d.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/assets/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;
    private final SecurityUtils securityUtils;

    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE','SYSTEM')")
    public ResponseEntity<ApiResponse<RequestDto>> createRequest(@Valid @RequestBody RequestDto requestDto) {
        RequestDto createdRequest = requestService.createRequest(requestDto);
        ApiResponse<RequestDto> response = new ApiResponse<>(HttpStatus.CREATED.value(), "Request created", createdRequest);
        
        response.add(linkTo(methodOn(RequestController.class).getRequestById(createdRequest.getRequestId())).withSelfRel());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('IT_SUPPORT_MEMBER','SYSTEM','EMPLOYEE')")
    public ResponseEntity<ApiResponse<Page<RequestDto>>> getAllRequests(Pageable pageable) {
        Page<RequestDto> requests = requestService.getAllRequests(
                pageable,
                securityUtils.getCurrentUserRole(),
                securityUtils.getCurrentUserIdLong());
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Requests fetched", requests));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('IT_SUPPORT_MEMBER','SYSTEM') or @authz.isRequestOwner(#id)")
    public ResponseEntity<ApiResponse<RequestDto>> getRequestById(@PathVariable String id) {
        RequestDto request = requestService.getRequestById(id);
        ApiResponse<RequestDto> response = new ApiResponse<>(HttpStatus.OK.value(), "Request fetched", request);
        
        Role role = securityUtils.getCurrentUserRole();
        String currentUserId = securityUtils.getCurrentUserId();

        response.add(linkTo(methodOn(RequestController.class).getRequestById(id)).withSelfRel());

        if (role == Role.IT_SUPPORT_MEMBER || role == Role.SYSTEM || currentUserId.equals(request.getRequestedForId())) {
            response.add(linkTo(methodOn(RequestController.class).updateRequest(id, null)).withRel("update-request"));
        }

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('IT_SUPPORT_MEMBER','SYSTEM') or @authz.isRequestOwner(#id)")
    public ResponseEntity<ApiResponse<RequestDto>> updateRequest(@PathVariable String id, @Valid @RequestBody RequestDto requestDto) {
        RequestDto updatedRequest = requestService.updateRequest(id, requestDto);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Request updated", updatedRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM')")
    public ResponseEntity<ApiResponse<Void>> deleteRequest(@PathVariable String id) {
        requestService.deleteRequest(id);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.NO_CONTENT.value(), "Request deleted", null));
    }
}