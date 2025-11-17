package com.globus.userservice.mapper;

import com.globus.userservice.dto.UserResponseDto;
import com.globus.userservice.entity.Privilege;
import com.globus.userservice.entity.Role;
import com.globus.userservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "roles", qualifiedByName = "getRolesNamesFromRoles", source = "roles")
    @Mapping(target = "privileges", qualifiedByName = "getPrivilegesNamesFromPrivileges", source = "privileges")
    UserResponseDto toUserResponseDto(User user);

    @Named("getRolesNamesFromRoles")
    default List<String> getRolesNames(List<Role> roles) {
        return roles.stream().map(Role::getRoleName).toList();
    }

    @Named("getPrivilegesNamesFromPrivileges")
    default List<String> getPrivilegesNames(List<Privilege> privileges) {
        return privileges.stream().map(Privilege::getName).toList();
    }
}
