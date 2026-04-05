package com.fareye.sphere.d.controllers;

import com.fareye.sphere.d.advices.ApiResponse;
import com.fareye.sphere.d.dtos.UserDto;
import com.fareye.sphere.d.entities.enums.Role;
import com.fareye.sphere.d.services.UserService;
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
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SecurityUtils securityUtils;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SYSTEM')")
    public ResponseEntity<ApiResponse<UserDto>> createUser(@Valid @RequestBody UserDto userDto) {
        UserDto createdUser = userService.createUser(userDto);
        ApiResponse<UserDto> response = new ApiResponse<>(HttpStatus.CREATED.value(), "User created successfully", createdUser);
        
        response.add(linkTo(methodOn(UserController.class).getUserById(createdUser.getUserId())).withSelfRel());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SYSTEM') or @authz.isSelf(#id)")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable String id) {
        UserDto user = userService.getUserById(id);
        ApiResponse<UserDto> response = new ApiResponse<>(HttpStatus.OK.value(), "User fetched", user);
        
        Role role = securityUtils.getCurrentUserRole();

        response.add(linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel());

        if (role == Role.ADMIN || role == Role.SYSTEM) {
            response.add(linkTo(methodOn(UserController.class).updateUser(id, null)).withRel("update-user"));
        }
        if (role == Role.ADMIN || role == Role.SYSTEM) {
            response.add(linkTo(methodOn(UserController.class).deleteUser(id)).withRel("delete-user"));
            response.add(linkTo(methodOn(UserController.class).getAllUsers(null)).withRel("all-users"));
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SYSTEM')")
    public ResponseEntity<ApiResponse<Page<UserDto>>> getAllUsers(Pageable pageable) {
        Page<UserDto> users = userService.getAllUsers(pageable);
        ApiResponse<Page<UserDto>> response = new ApiResponse<>(HttpStatus.OK.value(), "Users fetched", users);
        
        response.add(linkTo(methodOn(UserController.class).getAllUsers(pageable)).withSelfRel());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SYSTEM')")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(@PathVariable String id, @Valid @RequestBody UserDto userDto) {
        UserDto updatedUser = userService.updateUser(id, userDto);
        ApiResponse<UserDto> response = new ApiResponse<>(HttpStatus.OK.value(), "User updated", updatedUser);
        
        response.add(linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SYSTEM')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        ApiResponse<Void> response = new ApiResponse<>(HttpStatus.NO_CONTENT.value(), "User deleted", null);
        return ResponseEntity.ok(response);
    }
}