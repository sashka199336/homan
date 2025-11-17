package com.globus.userservice.service.impl;

import com.globus.userservice.entity.Privilege;
import com.globus.userservice.repository.PrivilegeRepository;
import com.globus.userservice.service.interfaces.PrivilegeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrivilegeServiceImpl implements PrivilegeService {

    private final PrivilegeRepository privilegeRepository;

    @Override
    public Privilege getPrivilegeByName(String name) {
        return privilegeRepository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException(String.format(
                        "Привилегия %s не найдена.", name)));
    }
}
