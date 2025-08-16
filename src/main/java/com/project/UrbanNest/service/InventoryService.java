package com.project.UrbanNest.service;

import com.project.UrbanNest.dto.*;
import com.project.UrbanNest.entity.Room;
import org.springframework.data.domain.Page;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface InventoryService  {

    void initializeRoomForAYear(Room room);

    void deleteAllInventories(Room room);

    Page<HotelPriceResponseDto> searchHotel(HotelSearchRequest hotelSearchRequest);

    List<InventoryDto> getAllInventoryByRoom(Long roomId);

    void updateInventory(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto);
}
