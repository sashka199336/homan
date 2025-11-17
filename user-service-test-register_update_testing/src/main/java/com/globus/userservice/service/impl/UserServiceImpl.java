package com.globus.userservice.service.impl;

import com.globus.userservice.dto.UserResponseDto;
import com.globus.userservice.dto.UsersDataDto;
import com.globus.userservice.entity.Role;
import com.globus.userservice.entity.User;
import com.globus.userservice.exception.IllegalUserStateException;
import com.globus.userservice.mapper.UserMapper;
import com.globus.userservice.repository.UserRepository;
import com.globus.userservice.service.interfaces.RoleService;
import com.globus.userservice.service.interfaces.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleService roleService;

    @Override
    @Transactional
    public UserResponseDto delete(UUID id) {
        User affectedUser = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Пользователь не найден"));
        if (affectedUser.getIsActive()) {
            affectedUser.setIsActive(false);
        } else {
            throw new IllegalUserStateException("Пользователь уже не активен");
        }
        return userMapper.toUserResponseDto(userRepository.save(affectedUser));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Пользователь не найден"));
        return userMapper.toUserResponseDto(user);
    }

    @Override
    @Transactional(readOnly=true)
    public Optional<User> getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public UserResponseDto register(UsersDataDto request) {
        User user = userMapper.userDataDtoToUser(request);
        user.setRoles(new ArrayList<>());
        if (request.getRoleName() != null) {
            Role role = roleService.getByName(request.getRoleName());
            user.getRoles().add(role);
        }
        user = userRepository.save(user);
        return userMapper.toUserResponseDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto assignRole(UUID id, String roleName) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Пользователь не найден"));
        Role role = roleService.getByName(roleName);
        user.getRoles().add(role);
        return userMapper.toUserResponseDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponseDto removeRole(UUID id, String roleName) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Пользователь не найден"));
        Role role = roleService.getByName(roleName);
        user.getRoles().removeIf(r -> r.equals(role));
        return userMapper.toUserResponseDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponseDto update(UUID id, UsersDataDto request) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Пользователь не найден"));
        user = userMapper.updateUser(request, user);
        user = userRepository.save(user);
        return userMapper.toUserResponseDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getByNamePart(String namePart) {
        return userRepository.findUsersByNamePart(namePart).stream()
                .map(userMapper::toUserResponseDto).toList();
    }
}
