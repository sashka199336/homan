package com.globus.userservice.service.interfaces;

import com.globus.userservice.entity.Role;
import com.globus.userservice.entity.User;

import java.util.List;

public interface RoleService {

    Role getRoleByName(String name);

    List<User> findUsersByRoleName(String name);
}
