package com.globus.userservice.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Role role;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        role = new Role(UUID.randomUUID(), "ADMIN", "Admin role", LocalDateTime.now());
        List<Role> roles = Arrays.asList(role);
        user = new User(id, "Ivan", "Ivanov", "ivan@example.com", "Developer", true, roles, createdAt, updatedAt);
    }

    @Test
    void getId() {
        assertEquals(id, user.getId());
    }

    @Test
    void getFirstName() {
        assertEquals("Ivan", user.getFirstName());
    }

    @Test
    void getLastName() {
        assertEquals("Ivanov", user.getLastName());
    }

    @Test
    void getEmail() {
        assertEquals("ivan@example.com", user.getEmail());
    }

    @Test
    void getPosition() {
        assertEquals("Developer", user.getPosition());
    }

    @Test
    void getIsActive() {
        assertTrue(user.getIsActive());
    }

    @Test
    void getRoles() {
        assertNotNull(user.getRoles());
        assertEquals(1, user.getRoles().size());
        assertEquals(role, user.getRoles().get(0));
    }

    @Test
    void getCreatedAt() {
        assertEquals(createdAt, user.getCreatedAt());
    }

    @Test
    void getUpdatedAt() {
        assertEquals(updatedAt, user.getUpdatedAt());
    }

    @Test
    void setId() {
        UUID newId = UUID.randomUUID();
        user.setId(newId);
        assertEquals(newId, user.getId());
    }

    @Test
    void setFirstName() {
        user.setFirstName("Petr");
        assertEquals("Petr", user.getFirstName());
    }

    @Test
    void setLastName() {
        user.setLastName("Petrov");
        assertEquals("Petrov", user.getLastName());
    }

    @Test
    void setEmail() {
        user.setEmail("petr@example.com");
        assertEquals("petr@example.com", user.getEmail());
    }

    @Test
    void setPosition() {
        user.setPosition("Manager");
        assertEquals("Manager", user.getPosition());
    }

    @Test
    void setIsActive() {
        user.setIsActive(false);
        assertFalse(user.getIsActive());
    }

    @Test
    void setRoles() {
        Role newRole = new Role(UUID.randomUUID(), "USER", "User role", LocalDateTime.now());
        List<Role> newRoles = Arrays.asList(newRole);
        user.setRoles(newRoles);
        assertEquals(newRole, user.getRoles().get(0));
    }

    @Test
    void setCreatedAt() {
        LocalDateTime newCreated = LocalDateTime.now().minusDays(2);
        user.setCreatedAt(newCreated);
        assertEquals(newCreated, user.getCreatedAt());
    }

    @Test
    void setUpdatedAt() {
        LocalDateTime newUpdated = LocalDateTime.now().plusDays(1);
        user.setUpdatedAt(newUpdated);
        assertEquals(newUpdated, user.getUpdatedAt());
    }
}