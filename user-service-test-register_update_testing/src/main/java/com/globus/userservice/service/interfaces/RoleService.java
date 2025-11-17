package com.globus.userservice.service.interfaces;

import com.globus.userservice.entity.Role;

public interface RoleService {

    Role getByName(String name);
}
