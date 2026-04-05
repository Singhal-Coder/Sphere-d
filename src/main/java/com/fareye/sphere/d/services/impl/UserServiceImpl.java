package com.fareye.sphere.d.services.impl;

import com.fareye.sphere.d.dtos.UserDto;
import com.fareye.sphere.d.entities.User;
import com.fareye.sphere.d.exceptions.InvalidIdException;
import com.fareye.sphere.d.exceptions.ResourceNotFoundException;
import com.fareye.sphere.d.mappers.UserMapper;
import com.fareye.sphere.d.repositories.UserRepository;
import com.fareye.sphere.d.services.UserService;
import com.fareye.sphere.d.utils.IdUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final IdUtils idUtils;

    @Override
    public UserDto createUser(UserDto userDto) {
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
        userRepository.delete(user);
    }
}