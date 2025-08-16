package com.project.UrbanNest.service;

import com.project.UrbanNest.dto.HotelDto;
import com.project.UrbanNest.dto.HotelInfoDto;
import com.project.UrbanNest.dto.HotelInfoRequestDto;

import java.util.List;

public interface HotelService {

    HotelDto createNewHotel(HotelDto hotelDto);

    HotelDto getHotelById(Long Id);

    HotelDto updateHotelById(Long id,HotelDto hotelDto);

    void deleteHotelById(Long id);

    void activeHotel( Long hotelId);

    HotelInfoDto getHotelInfoById(Long hotelId, HotelInfoRequestDto hotelInfoRequestDto);

    List<HotelDto> getAllHotels();
}
