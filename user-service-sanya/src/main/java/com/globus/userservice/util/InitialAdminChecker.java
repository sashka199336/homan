package com.globus.userservice.util;

import com.globus.userservice.dto.UserRegistrationRequest;
import com.globus.userservice.entity.User;
import com.globus.userservice.service.interfaces.RoleService;
import com.globus.userservice.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class InitialAdminChecker implements ApplicationRunner {

    private final UserService userService;
    private final RoleService roleService;

    @Value("${app.first_run.generate.admin}")
    private String adminRole;

    /**
     * Данный метод запускается автоматически при запуске приложения, проверяет,
     * есть ли в базе данных хотя бы один пользователь с ролью admin. Если таковых нет,
     * помогает создать первого админа через консоль. После чего приложение работает
     * в штатном режиме, и созданный админ регистрирует новых пользователей,
     * назначает им роли и привилегии и т.д.
     *
     * @param args Не используется
     */
    @Override
    public void run(ApplicationArguments args) {
        List<User> userList = roleService.findUsersByRoleName(adminRole);
        if (userList.isEmpty()) {
            System.out.println("⚠ Администратор c ролью " + adminRole + " не найден в базе.");

            Scanner scanner = new Scanner(System.in);

            System.out.print("\"firstName\": \"");
            String firstName = scanner.nextLine();

            System.out.print("\"lastName\": \"");
            String lastName = scanner.nextLine();

            System.out.print("\"position\": \"");
            String position = scanner.nextLine();

            System.out.print("\"email\": \"");
            String email = scanner.nextLine();

            System.out.print("\"password\": \"");
            String password = scanner.nextLine();

            UserRegistrationRequest request = new UserRegistrationRequest();
            request.setFirstName(firstName);
            request.setLastName(lastName);
            request.setPosition(position);
            request.setEmail(email);
            request.setRoleName(adminRole);
            request.setPassword(password);

            userService.registerUser(request);
        }
    }
}