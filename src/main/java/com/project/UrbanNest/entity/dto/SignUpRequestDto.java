package com.project.UrbanNest.dto;

import com.project.UrbanNest.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDto {
    private String email;
    private String password;
    private String name;
    private Set<Role> roles;

}
