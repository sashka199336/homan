package com.globus.userservice.service.interfaces;

import com.globus.userservice.dto.UsersDataDto;
import com.globus.userservice.dto.UserResponseDto;
import com.globus.userservice.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    UserResponseDto delete(UUID id);

    UserResponseDto register(UsersDataDto request);

    UserResponseDto getById(UUID id);

    Optional<User> getByEmail(String email);

    UserResponseDto update(UUID id, UsersDataDto request);

    UserResponseDto assignRole(UUID id, String roleName);

    UserResponseDto removeRole(UUID id, String roleName);

    List<UserResponseDto> getByNamePart(String namePart);
}
