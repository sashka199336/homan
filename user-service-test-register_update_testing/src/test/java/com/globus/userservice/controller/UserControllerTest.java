package com.globus.userservice.controller;

import com.globus.userservice.dto.UsersDataDto;
import com.globus.userservice.dto.UserResponseDto;
import com.globus.userservice.service.interfaces.UserService;
import com.globus.userservice.mapper.UserMapper;
import com.globus.userservice.validator.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    UserService userService;
    UserMapper userMapper;
    UserValidator userValidator;
    UserController controller;

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserService.class);
        userMapper = Mockito.mock(UserMapper.class);
        userValidator = Mockito.mock(UserValidator.class);
        controller = new UserController(userService, userMapper, userValidator);
    }

    @Test
    void deleteUser() {
        UUID id = UUID.randomUUID();
        UserResponseDto resp = new UserResponseDto();
        Mockito.when(userService.delete(id)).thenReturn(resp);

        assertSame(resp, controller.deleteUser(id));
        Mockito.verify(userService).delete(id);
    }

    @Test
    void register() {
        UsersDataDto req = new UsersDataDto();
        UserResponseDto resp = new UserResponseDto();
        Mockito.doNothing().when(userValidator).validate(req);
        Mockito.when(userService.register(req)).thenReturn(resp);

        assertSame(resp, controller.register(req));
        Mockito.verify(userValidator).validate(req);
        Mockito.verify(userService).register(req);
    }

    @Test
    void assignRole() {
        UUID id = UUID.randomUUID();
        String role = "ADMIN";
        UserResponseDto resp = new UserResponseDto();
        Mockito.when(userService.assignRole(id, role)).thenReturn(resp);

        assertSame(resp, controller.assignRole(id, role));
        Mockito.verify(userService).assignRole(id, role);
    }

    @Test
    void removeRole() {
        UUID id = UUID.randomUUID();
        String role = "USER";
        UserResponseDto resp = new UserResponseDto();
        Mockito.when(userService.removeRole(id, role)).thenReturn(resp);

        assertSame(resp, controller.removeRole(id, role));
        Mockito.verify(userService).removeRole(id, role);
    }

    @Test
    void getProfile() {
        UUID id = UUID.randomUUID();
        UserResponseDto resp = new UserResponseDto();
        Mockito.when(userService.getById(id)).thenReturn(resp);

        assertSame(resp, controller.getProfile(id));
        Mockito.verify(userService).getById(id);
    }

    @Test
    void updateProfile() {
        UUID id = UUID.randomUUID();
        UsersDataDto req = new UsersDataDto();
        UserResponseDto resp = new UserResponseDto();
        Mockito.when(userService.update(id, req)).thenReturn(resp);

        assertSame(resp, controller.updateProfile(id, req));
        Mockito.verify(userService).update(id, req);
    }

    @Test
    void search() {
        String part = "vas";
        List<UserResponseDto> resp = Arrays.asList(new UserResponseDto(), new UserResponseDto());
        Mockito.when(userService.getByNamePart(part)).thenReturn(resp);

        assertSame(resp, controller.search(part));
        Mockito.verify(userService).getByNamePart(part);
    }
}