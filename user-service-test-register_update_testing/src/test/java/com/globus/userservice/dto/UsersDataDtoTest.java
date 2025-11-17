package com.globus.userservice.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsersDataDtoTest {

    private UsersDataDto dto;

    @BeforeEach
    void setUp() {
        dto = new UsersDataDto("Ivan", "Ivanov", "Developer", "ivan@example.com", "USER");
    }

    @Test
    void getFirstName() {
        assertEquals("Ivan", dto.getFirstName());
    }

    @Test
    void getLastName() {
        assertEquals("Ivanov", dto.getLastName());
    }

    @Test
    void getPosition() {
        assertEquals("Developer", dto.getPosition());
    }

    @Test
    void getEmail() {
        assertEquals("ivan@example.com", dto.getEmail());
    }

    @Test
    void getRoleName() {
        assertEquals("USER", dto.getRoleName());
    }

    @Test
    void setFirstName() {
        dto.setFirstName("Petr");
        assertEquals("Petr", dto.getFirstName());
    }

    @Test
    void setLastName() {
        dto.setLastName("Petrov");
        assertEquals("Petrov", dto.getLastName());
    }

    @Test
    void setPosition() {
        dto.setPosition("Manager");
        assertEquals("Manager", dto.getPosition());
    }

    @Test
    void setEmail() {
        dto.setEmail("petrov@example.com");
        assertEquals("petrov@example.com", dto.getEmail());
    }

    @Test
    void setRoleName() {
        dto.setRoleName("ADMIN");
        assertEquals("ADMIN", dto.getRoleName());
    }

    @Test
    void testEquals() {

        UsersDataDto dto2 = new UsersDataDto("Ivan", "Ivanov", "Developer", "ivan@example.com", "USER");
        assertEquals(dto, dto2);


        assertNotEquals(dto, null);
        assertNotEquals(dto, "string");
    }

    @Test
    void canEqual() {
        UsersDataDto dto2 = new UsersDataDto();
        assertTrue(dto.canEqual(dto2));

        assertFalse(dto.canEqual("string"));
    }

    @Test
    void testHashCode() {
        UsersDataDto dto2 = new UsersDataDto("Ivan", "Ivanov", "Developer", "ivan@example.com", "USER");
        assertEquals(dto.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        String str = dto.toString();
        assertTrue(str.contains("Ivan"));
        assertTrue(str.contains("Ivanov"));
        assertTrue(str.contains("Developer"));
        assertTrue(str.contains("ivan@example.com"));
        assertTrue(str.contains("USER"));
    }
}