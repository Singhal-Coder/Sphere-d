package com.fareye.sphere.d.services.impl;

import com.fareye.sphere.d.dtos.UserDto;
import com.fareye.sphere.d.entities.User;
import com.fareye.sphere.d.entities.enums.AssetStatus;
import com.fareye.sphere.d.entities.enums.BookingStatus;
import com.fareye.sphere.d.exceptions.DuplicateResourceException;
import com.fareye.sphere.d.exceptions.InvalidIdException;
import com.fareye.sphere.d.exceptions.ResourceInUseException;
import com.fareye.sphere.d.exceptions.ResourceNotFoundException;
import com.fareye.sphere.d.mappers.UserMapper;
import com.fareye.sphere.d.repositories.UserRepository;
import com.fareye.sphere.d.services.UserService;
import com.fareye.sphere.d.utils.IdUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final IdUtils idUtils;

    @Override
    public UserDto createUser(UserDto userDto) {
        // CHECK: Email Uniqueness
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new DuplicateResourceException("User", "email", userDto.getEmail());
        }

        User user = userMapper.toEntity(userDto);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDto getUserById(String userId) {
        Long id = idUtils.parseUserId(userId)
                .orElseThrow(() -> new InvalidIdException(userId));

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return userMapper.toDto(user);
    }

    @Override
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDto);
    }

    @Override
    public UserDto updateUser(String userId, UserDto userDto) {
        Long id = idUtils.parseUserId(userId)
                .orElseThrow(() -> new InvalidIdException(userId));

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // CHECK: Email Uniqueness on Update
        Optional<User> existingUserWithEmail = userRepository.findByEmail(userDto.getEmail());
        if (existingUserWithEmail.isPresent() && !existingUserWithEmail.get().getUserId().equals(id)) {
            throw new DuplicateResourceException("User", "email", userDto.getEmail());
        }

        userMapper.updateUserFromDto(userDto, user);
        User updatedUser = userRepository.save(user);

        return userMapper.toDto(updatedUser);
    }

    @Override
    public void deleteUser(String userId) {
        Long id = idUtils.parseUserId(userId)
                .orElseThrow(() -> new InvalidIdException(userId));
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        boolean hasAssignedAssets = user.getAssets() != null && user.getAssets().stream()
                .anyMatch(asset -> asset.getStatus() == AssetStatus.ASSIGNED && asset.getIsActive());
        
        if (hasAssignedAssets) {
            throw new ResourceInUseException("Cannot delete user. Please unassign their active assets first.");
        }

        // CHECK: Cannot delete if user has future active bookings
        boolean hasFutureBookings = user.getBookings() != null && user.getBookings().stream()
                .anyMatch(booking -> booking.getStatus() == BookingStatus.ACTIVE && !booking.getBookedDate().isBefore(LocalDate.now()));
        
        if (hasFutureBookings) {
            throw new ResourceInUseException("Cannot delete user with active future bookings. Please cancel them first.");
        }

        userRepository.delete(user);
    }
}