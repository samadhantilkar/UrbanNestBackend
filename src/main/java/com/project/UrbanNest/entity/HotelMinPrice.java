package com.project.UrbanNest.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class HotelMinPrice extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, precision = 10,scale = 2)
    private BigDecimal price; //cheapest room price on a particular day

    public HotelMinPrice(Hotel hotel,LocalDate date){
        this.hotel=hotel;
        this.date=date;
    }

}
