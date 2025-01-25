package com.project.UrbanNest.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.UrbanNest.entity.Guest;
import com.project.UrbanNest.entity.Hotel;
import com.project.UrbanNest.entity.Room;
import com.project.UrbanNest.entity.User;
import com.project.UrbanNest.entity.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
public class BookingDto  {

    private Long id;

    private Integer roomCount;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    private BookingStatus bookingStatus;

    private Set<GuestDto> guests;

    private BigDecimal amount;
}
