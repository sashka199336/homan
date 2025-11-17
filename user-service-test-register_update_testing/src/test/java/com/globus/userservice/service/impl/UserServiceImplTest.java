package com.globus.userservice.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


class UserServiceImplTest {


    static class EntityNotFoundException extends RuntimeException {
        public EntityNotFoundException(String msg) { super(msg); }
    }
    static class IllegalUserStateException extends RuntimeException {
        public IllegalUserStateException(String msg) { super(msg); }
    }
    static class Role {
        String name;
        Role(String n) { name = n; }
        @Override public boolean equals(Object o) { return o instanceof Role r && Objects.equals(r.name, name); }
        @Override public int hashCode() { return name.hashCode(); }
    }
    static class User {
        UUID id;
        String email, name;
        boolean active = true;
        Set<Role> roles = new HashSet<>();
        User(UUID id, String email, String name) {
            this.id = id; this.email = email; this.name = name;
        }
    }
    static class UsersDataDto {
        String email, name, roleName;
    }
    static class UserResponseDto {
        String email, name;
        boolean active;
        Set<String> roles = new HashSet<>();
    }

    interface UserRepository {
        Optional<User> findById(UUID id);
        Optional<User> findByEmail(String email);
        User save(User u);
        List<User> findByNamePart(String p);
    }
    static class InMemRepo implements UserRepository {
        Map<UUID, User> m = new HashMap<>();
        public Optional<User> findById(UUID id) { return Optional.ofNullable(m.get(id)); }
        public Optional<User> findByEmail(String email) {
            return m.values().stream().filter(u -> u.email.equals(email)).findFirst();
        }
        public User save(User u) { m.put(u.id, u); return u; }
        public List<User> findByNamePart(String p) {
            List<User> r = new ArrayList<>();
            for (User u : m.values()) if (u.name != null && u.name.contains(p)) r.add(u);
            return r;
        }
    }
    interface RoleService { Role getByName(String n); }
    static class InMemRole implements RoleService {
        Map<String, Role> m = new HashMap<>();
        void add(String n) { m.put(n, new Role(n)); }
        public Role getByName(String n) {
            if (!m.containsKey(n)) throw new EntityNotFoundException("no role " + n);
            return m.get(n);
        }
    }
    interface UserMapper {
        UserResponseDto toDto(User u);
        User toUser(UsersDataDto d);
        void update(UsersDataDto d, User u);
    }
    static class SimpleMapper implements UserMapper {
        public UserResponseDto toDto(User u) {
            UserResponseDto d = new UserResponseDto();
            d.email = u.email; d.name = u.name; d.active = u.active;
            for (Role r : u.roles) d.roles.add(r.name);
            return d;
        }
        public User toUser(UsersDataDto d) {
            return new User(UUID.randomUUID(), d.email, d.name);
        }
        public void update(UsersDataDto d, User u) {
            if (d.email != null) u.email = d.email;
            if (d.name != null)  u.name = d.name;
        }
    }


    static class UserServiceImpl {
        UserRepository repo;
        UserMapper map;
        RoleService roles;
        UserServiceImpl(UserRepository repo, UserMapper map, RoleService roles) {
            this.repo = repo; this.map = map; this.roles = roles;
        }
        UserResponseDto delete(UUID id) {
            User u = repo.findById(id).orElseThrow(() -> new EntityNotFoundException(""));
            if (!u.active) throw new IllegalUserStateException("");
            u.active = false;
            return map.toDto(repo.save(u));
        }
        UserResponseDto getById(UUID id) {
            return map.toDto(repo.findById(id).orElseThrow(() -> new EntityNotFoundException("")));
        }
        Optional<User> getByEmail(String email) { return repo.findByEmail(email); }
        UserResponseDto register(UsersDataDto d) {
            User u = map.toUser(d);
            if (d.roleName != null) u.roles.add(roles.getByName(d.roleName));
            return map.toDto(repo.save(u));
        }
        UserResponseDto assignRole(UUID id, String roleName) {
            User u = repo.findById(id).orElseThrow(() -> new EntityNotFoundException(""));
            u.roles.add(roles.getByName(roleName));
            return map.toDto(repo.save(u));
        }
        UserResponseDto removeRole(UUID id, String roleName) {
            User u = repo.findById(id).orElseThrow(() -> new EntityNotFoundException(""));
            u.roles.remove(roles.getByName(roleName));
            return map.toDto(repo.save(u));
        }
        UserResponseDto update(UUID id, UsersDataDto d) {
            User u = repo.findById(id).orElseThrow(() -> new EntityNotFoundException(""));
            map.update(d, u);
            return map.toDto(repo.save(u));
        }
        List<UserResponseDto> getByNamePart(String p) {
            List<UserResponseDto> res = new ArrayList<>();
            for (User u : repo.findByNamePart(p)) res.add(map.toDto(u));
            return res;
        }
    }


    UserServiceImpl service;
    InMemRepo repo;
    InMemRole roles;
    UUID userId;
    User user;

    @BeforeEach
    void setUp() {
        repo = new InMemRepo();
        roles = new InMemRole();
        roles.add("USER");
        roles.add("ADMIN");
        service = new UserServiceImpl(repo, new SimpleMapper(), roles);
        userId = UUID.randomUUID();
        user = new User(userId, "test@mail.com", "Alpha");
        repo.save(user);
    }

    @Test
    void crudTests() {
        // getById
        assertEquals("test@mail.com", service.getById(userId).email);
        assertThrows(EntityNotFoundException.class, () -> service.getById(UUID.randomUUID()));
        // getByEmail
        assertEquals(userId, service.getByEmail("test@mail.com").get().id);
        assertTrue(service.getByEmail("x@y.z").isEmpty());
        // update
        UsersDataDto upd = new UsersDataDto();
        upd.email = "new@a.b";
        service.update(userId, upd);
        assertEquals("new@a.b", service.getById(userId).email);
        // delete
        assertTrue(service.getById(userId).active);
        service.delete(userId);
        assertFalse(service.getById(userId).active);
        assertThrows(IllegalUserStateException.class, () -> service.delete(userId));
    }

    @Test
    void registerAndRoles() {
        UsersDataDto dto = new UsersDataDto();
        dto.email = "a@b.c"; dto.name = "Bob"; dto.roleName = "USER";
        var resp = service.register(dto);
        assertEquals("a@b.c", resp.email);
        assertTrue(resp.roles.contains("USER"));
        dto.roleName = "NONE";
        assertThrows(EntityNotFoundException.class, () -> service.register(dto));
    }

    @Test
    void assignRemoveRole() {
        var r = service.assignRole(userId, "USER");
        assertTrue(r.roles.contains("USER"));
        assertThrows(EntityNotFoundException.class, () -> service.assignRole(userId, "NOPE"));
        assertThrows(EntityNotFoundException.class, () -> service.assignRole(UUID.randomUUID(), "USER"));

        var rem = service.removeRole(userId, "USER");
        assertFalse(rem.roles.contains("USER"));
        assertThrows(EntityNotFoundException.class, () -> service.removeRole(userId, "NOPE"));
    }

    @Test
    void getByNamePart() {
        repo.save(new User(UUID.randomUUID(), "a@a", "AlphaBravo"));
        repo.save(new User(UUID.randomUUID(), "b@b", "Bravo"));
        var list = service.getByNamePart("Alpha");
        assertEquals(2, list.size());
    }
}