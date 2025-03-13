package com.project.UrbanNest.dto;

import com.project.UrbanNest.entity.User;
import com.project.UrbanNest.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.Data;

@Data
public class GuestDto {
    private Long id;
    private User user;

    private String name;

    private Gender gender;

    private Integer age;
}
