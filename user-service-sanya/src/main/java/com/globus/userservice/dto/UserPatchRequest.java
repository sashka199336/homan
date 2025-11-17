package com.globus.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPatchRequest {
    private String firstName;
    private String lastName;
    private String position;
    private String email;
    private String roleName;
}