package com.globus.userservice.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    private Role role;
    private UUID id;
    private LocalDateTime created;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        created = LocalDateTime.now();
        role = new Role(id, "ADMIN", "Administration role", created);
    }

    @Test
    void getRoleId() {
        assertEquals(id, role.getRoleId());
    }

    @Test
    void getRoleName() {
        assertEquals("ADMIN", role.getRoleName());
    }

    @Test
    void getDescription() {
        assertEquals("Administration role", role.getDescription());
    }

    @Test
    void getCreatedAt() {
        assertEquals(created, role.getCreatedAt());
    }

    @Test
    void setRoleId() {
        UUID newId = UUID.randomUUID();
        role.setRoleId(newId);
        assertEquals(newId, role.getRoleId());
    }

    @Test
    void setRoleName() {
        role.setRoleName("USER");
        assertEquals("USER", role.getRoleName());
    }

    @Test
    void setDescription() {
        role.setDescription("User role");
        assertEquals("User role", role.getDescription());
    }

    @Test
    void setCreatedAt() {
        LocalDateTime newTime = LocalDateTime.now().plusDays(1);
        role.setCreatedAt(newTime);
        assertEquals(newTime, role.getCreatedAt());
    }
}