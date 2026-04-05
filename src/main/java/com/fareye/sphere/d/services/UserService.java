package com.fareye.sphere.d.services;

import com.fareye.sphere.d.dtos.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserDto createUser(UserDto userDto);
    UserDto getUserById(String userId);
    Page<UserDto> getAllUsers(Pageable pageable);
    UserDto updateUser(String userId, UserDto userDto);
    void deleteUser(String userId);
}