package com.project.UrbanNest.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
@Entity
@Table(
//        indexes = {},
        uniqueConstraints = @UniqueConstraint(
            name = "unique_hotel_room_date",
            columnNames = {"hotel_id","room_id","date"}))

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inventory extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "hotel_id",nullable = false)
    private Hotel hotel;

    @ManyToOne
    @JoinColumn(name = "room_id",nullable = false)
    private Room room;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false,columnDefinition = "INTEGER DEFAULT 0")
    private Integer bookedCount;

    @Column(nullable = false,columnDefinition = "INTEGER DEFAULT 0")
    private Integer totalCount;

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer reservedCount;

    @Column(nullable = false, precision = 5,scale = 2)
    private BigDecimal surgeFactor;

    @Column(nullable = false,precision = 10,scale = 2)
    private BigDecimal price;  //basePrice * surgeFactor

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private Boolean closed;
}
