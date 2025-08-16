package com.project.UrbanNest.dto;

import com.project.UrbanNest.entity.enums.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class GuestDto {
//    private User user;
    private Long id;

    private String name;

    private Gender gender;

    private LocalDate date;
}
