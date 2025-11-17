package com.globus.userservice.service.interfaces;

import com.globus.userservice.entity.Privilege;

public interface PrivilegeService {

    Privilege getPrivilegeByName(String name);
}
