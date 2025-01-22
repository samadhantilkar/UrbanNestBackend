package com.project.UrbanNest.service;

import com.project.UrbanNest.dto.HotelDto;
import com.project.UrbanNest.dto.HotelPriceDto;
import com.project.UrbanNest.dto.HotelSearchRequest;
import com.project.UrbanNest.entity.HotelMinPrice;
import com.project.UrbanNest.entity.Room;
import org.springframework.data.domain.Page;;

public interface InventoryService  {

    void initializeRoomForAYear(Room room);

    void deleteAllInventories(Room room);

    Page<HotelPriceDto> searchHotel(HotelSearchRequest hotelSearchRequest);
}
