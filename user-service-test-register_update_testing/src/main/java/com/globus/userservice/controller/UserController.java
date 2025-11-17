package com.globus.userservice.controller;

import com.globus.userservice.dto.UsersDataDto;
import com.globus.userservice.dto.UserResponseDto;
import com.globus.userservice.mapper.UserMapper;
import com.globus.userservice.service.interfaces.UserService;
import com.globus.userservice.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final UserValidator userValidator;

    @DeleteMapping("/{id}")
    public UserResponseDto deleteUser(@PathVariable UUID id) {
        return userService.delete(id);
    }

    @PostMapping("/register")
    public UserResponseDto register(@RequestBody UsersDataDto request) {
        userValidator.validate(request);
        return userService.register(request);
    }

    @PatchMapping("/{id}/roles/add")
    public UserResponseDto assignRole(@PathVariable UUID id,
                                      @RequestParam (name = "role_name") String roleName) {
        return userService.assignRole(id, roleName);
    }

    @PatchMapping("/{id}/roles/remove")
    public UserResponseDto removeRole(@PathVariable UUID id,
                                      @RequestParam (name = "role_name") String roleName) {
        return userService.removeRole(id, roleName);
    }

    @GetMapping("/{id}")
    public UserResponseDto getProfile(@PathVariable UUID id) {
        return userService.getById(id);
    }

    @PutMapping("/{id}")
    public UserResponseDto updateProfile(@PathVariable UUID id,
                                         @RequestBody UsersDataDto request) {
        return userService.update(id, request);
    }

    @GetMapping("/by-name")
    public List<UserResponseDto> search(@RequestParam (name = "name_part") String namePart) {
        return userService.getByNamePart(namePart);
    }
}
