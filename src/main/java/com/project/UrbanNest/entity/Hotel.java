package com.project.UrbanNest.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "hotel")
public class Hotel extends BaseEntity{

    @Column(nullable = false)
    private String name;

    private String city;

    @Column(columnDefinition = "TEXT[]")
    private String[] photos;

    @Column(columnDefinition = "TEXT[]")
    private String[] amenities;

    @Embedded
    private HotelContactInfo contactInfo;

    @OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY   )
    @JsonIgnore
    private List<Room> rooms;

    private Boolean active;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User owner;

}
