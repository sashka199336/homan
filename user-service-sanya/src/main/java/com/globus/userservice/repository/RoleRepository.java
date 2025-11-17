package com.globus.userservice.repository;

import com.globus.userservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByRoleName(String roleName); // <-- Искомое поле

    @Query("select r from Role r left join fetch r.users where r.roleName = :roleName")
    Role findAllFieldsByRoleName(String roleName);
}