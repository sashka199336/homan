package com.globus.userservice.service.interfaces;

import com.globus.userservice.dto.AuthRequest;
import com.globus.userservice.dto.UserPatchRequest;
import com.globus.userservice.dto.UserRegistrationRequest;
import com.globus.userservice.dto.UserResponseDto;
import com.globus.userservice.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponseDto deleteUser(UUID id, UUID user_id);

    UUID registerUser(UserRegistrationRequest req);

    User getUserById(UUID id);

    UserResponseDto updateUser(UUID id, UserPatchRequest req);

    UserResponseDto assignRole(UUID id, String roleName);

    UserResponseDto removeRole(UUID id, String roleName);

    UserResponseDto assignPrivilege(UUID id, String privilegeName);

    UserResponseDto removePrivilege(UUID id, String privilegeName);

    List<UserResponseDto> searchUsers(String search);

    User getUserByEmail(String email);

    void updatePasswordByEmail(AuthRequest request);
}
