package com.project.UrbanNest.dto;

import com.project.UrbanNest.entity.enums.Role;
import lombok.Data;

import java.util.Set;

@Data
public class SignUpRequestDto {
    private String email;
    private String password;
    private String name;
    private Set<Role> roles;

}
