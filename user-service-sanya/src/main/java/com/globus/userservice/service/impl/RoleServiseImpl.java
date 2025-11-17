package com.globus.userservice.service.impl;

import com.globus.userservice.entity.Role;
import com.globus.userservice.entity.User;
import com.globus.userservice.repository.RoleRepository;
import com.globus.userservice.service.interfaces.RoleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiseImpl implements RoleService {

    private final RoleRepository roleRepository;

    /**
     * Поиск роли по имени
     *
     * @param name Название роли
     * @return Сущность роли
     * @throws EntityNotFoundException Если роль не найдена
     */
    @Override
    public Role getRoleByName(String name) {
        return roleRepository.findByRoleName(name).orElseThrow(
                () -> new EntityNotFoundException(String.format("Роль %s не найдена", name)));
    }

    /**
     * Поиск списка пользователей, имеющих в списке ролей конкретную роль
     *
     * @param name Название роли
     * @return Список пользователей
     */
    @Override
    public List<User> findUsersByRoleName(String name) {
        return roleRepository.findAllFieldsByRoleName(name).getUsers();
    }
}
