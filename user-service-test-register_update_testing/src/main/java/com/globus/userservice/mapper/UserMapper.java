package com.globus.userservice.mapper;

import com.globus.userservice.dto.UserResponseDto;
import com.globus.userservice.dto.UsersDataDto;
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
    UserResponseDto toUserResponseDto(User user);

    @Mapping(target = "isActive", constant = "true")
    User userDataDtoToUser (UsersDataDto usersDataDto);

    default User updateUser(UsersDataDto usersDataDto, User user) {
        if (usersDataDto.getFirstName() != null) user.setFirstName(usersDataDto.getFirstName());
        if (usersDataDto.getLastName() != null) user.setLastName(usersDataDto.getLastName());
        if (usersDataDto.getPosition() != null) user.setPosition(usersDataDto.getPosition());
        if (usersDataDto.getEmail() != null) user.setEmail(usersDataDto.getEmail());
        return user;
    }

    @Named("getRolesNamesFromRoles")
    default List<String> getRolesNames(List<Role> roles) {
        return roles.stream().map(Role::getRoleName).toList();
    }
}
