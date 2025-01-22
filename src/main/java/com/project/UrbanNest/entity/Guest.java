package com.project.UrbanNest.entity;

import com.project.UrbanNest.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Guest extends BaseEntity {

    @JoinColumn(name = "user_id")
    @ManyToOne
    private User user;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Integer age;

}
