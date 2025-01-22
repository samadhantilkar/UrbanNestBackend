package com.project.UrbanNest.service;

import com.project.UrbanNest.dto.HotelDto;
import com.project.UrbanNest.dto.HotelInfoDto;

public interface HotelService {

    HotelDto createNewHotel(HotelDto hotelDto);

    HotelDto findHotelById(Long Id);

    HotelDto updateHotelById(Long id,HotelDto hotelDto);

    void deleteHotelById(Long id);

    void activeHotel( Long hotelId);

    HotelInfoDto getHotelInfoById(Long hotelId);
}
