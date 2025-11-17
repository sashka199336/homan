package com.globus.userservice.controller;

import com.globus.userservice.dto.AuthRequest;
import com.globus.userservice.dto.UserPatchRequest;
import com.globus.userservice.dto.UserRegistrationRequest;
import com.globus.userservice.dto.UserResponseDto;
import com.globus.userservice.mapper.UserMapper;
import com.globus.userservice.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    // Деактивация сотрудника
    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponseDto> deleteUser(@PathVariable UUID id, @RequestParam UUID user_id) {
        return ResponseEntity.ok(userService.deleteUser(id, user_id));
    }

    // Регистрация нового сотрудника
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @RequestBody UserRegistrationRequest req,
            @RequestHeader(value = "X-Request-Id", required = false) String requestId
    ) {
        UUID userId = userService.registerUser(req);
        Map<String, Object> resp = new HashMap<>();
        resp.put("message", "Пользователь успешно зарегистрирован");
        resp.put("userId", userId.toString());
        if (requestId != null) resp.put("requestId", requestId);
        return ResponseEntity.ok(resp);
    }

    // Назначение ролей
    @PatchMapping("/{id}/role/add")
    public ResponseEntity<UserResponseDto> assignRole(
            @PathVariable UUID id,
            @RequestParam (name = "role_name") String roleName) {
        UserResponseDto response = userService.assignRole(id, roleName);
        return ResponseEntity.ok(response);
    }

    // Удаление у пользователя ролей
    @PatchMapping("/{id}/role/remove")
    public ResponseEntity<UserResponseDto> removeRole(
            @PathVariable UUID id,
            @RequestParam (name = "role_name") String roleName) {
        UserResponseDto response = userService.removeRole(id, roleName);
        return ResponseEntity.ok(response);
    }

    // Назначение привилегий
    @PatchMapping("/{id}/privilege/add")
    public ResponseEntity<UserResponseDto> assignPrivilege(
            @PathVariable UUID id,
            @RequestParam (name = "privilege_name") String name) {
        UserResponseDto response = userService.assignPrivilege(id, name);
        return ResponseEntity.ok(response);
    }

    // Удаление у пользователя привилегий
    @PatchMapping("/{id}/privilege/remove")
    public ResponseEntity<UserResponseDto> removePrivilege(
            @PathVariable UUID id,
            @RequestParam (name = "privilege_name") String name) {
        UserResponseDto response = userService.removePrivilege(id, name);
        return ResponseEntity.ok(response);
    }

    // Просмотр профиля
    @GetMapping("/{id}")
    public ResponseEntity<?> getProfile(@PathVariable UUID id) {
        UserResponseDto response = userMapper.toUserResponseDto(userService.getUserById(id));
        return ResponseEntity.ok(response);
    }

    // Изменение профиля
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(
            @PathVariable UUID id,
            @RequestBody UserPatchRequest req) {
        UserResponseDto updatedUser = userService.updateUser(id, req);
        return ResponseEntity.ok(updatedUser);
    }

    // Поиск сотрудников
    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam String search,
            @RequestHeader(value = "X-Request-Id", required = false) String requestId
    ) {
        List<UserResponseDto> results = userService.searchUsers(search);
        Map<String, Object> resp = new HashMap<>();
        resp.put("results", results);
        if (requestId != null) resp.put("requestId", requestId);
        return ResponseEntity.ok(resp);
    }

    // Изменение пароля
    @PutMapping("/password/change")
    public void changePassword(@RequestBody AuthRequest request) {
        userService.updatePasswordByEmail(request);
    }
}
