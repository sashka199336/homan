package com.globus.userservice.service.impl;

import com.globus.userservice.entity.Role;
import com.globus.userservice.repository.RoleRepository;
import com.globus.userservice.service.interfaces.RoleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public Role getByName(String name) {
        return roleRepository.findByRoleName(name).orElseThrow(
                () -> new EntityNotFoundException(String.format("Роль %s не найдена", name)));
    }
}
