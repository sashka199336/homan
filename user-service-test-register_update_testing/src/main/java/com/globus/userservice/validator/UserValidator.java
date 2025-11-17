package com.globus.userservice.validator;

import com.globus.userservice.dto.UsersDataDto;
import com.globus.userservice.entity.User;
import com.globus.userservice.exception.ValidationException;
import com.globus.userservice.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserService userService;

    public void validate(UsersDataDto usersDataDto) {
        List<String> errorMessages = new ArrayList<>();

        if (usersDataDto.getFirstName() == null || usersDataDto.getFirstName().isBlank()) {
            errorMessages.add("Имя пользователя не заполнено");
        }
        if (usersDataDto.getLastName() == null || usersDataDto.getLastName().isBlank()) {
            errorMessages.add("Фамилия пользователя не заполнена");
        }
        if (usersDataDto.getPosition() == null || usersDataDto.getPosition().isBlank()) {
            errorMessages.add("Должность пользователя не заполнена");
        }
        if (usersDataDto.getRoleName() == null || usersDataDto.getRoleName().isBlank()) {
            errorMessages.add("Роль пользователя не заполнена");
        }
        if (usersDataDto.getEmail() == null || usersDataDto.getEmail().isBlank()) {
            errorMessages.add("Email пользователя не заполнен");
        } else {
            Optional<User> user = userService.getByEmail(usersDataDto.getEmail());
            if (user.isPresent()) {
                errorMessages.add(String.format("Пользователь с email: %s уже существует", usersDataDto.getEmail()));
            }
        }
        if (!errorMessages.isEmpty()) {
            throw new ValidationException(String.join("; ", errorMessages));
        }
    }
}
