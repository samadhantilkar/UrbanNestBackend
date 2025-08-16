package com.project.UrbanNest.dto;

import com.project.UrbanNest.entity.HotelContactInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelPriceResponseDto {
    private Long id;
    private String name;
    private String city;
    private String[] photo;
    private String[] amenities;
    private HotelContactInfo contactInfo;
    private Double price;
}
