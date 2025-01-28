package com.project.UrbanNest.service;

import com.project.UrbanNest.dto.RoomDto;

import java.util.List;

public interface RoomService {
    RoomDto createNewRoom(Long hotelId,RoomDto roomDto);

    List<RoomDto> getAllRoomInHotel(Long hotelId);

    RoomDto getRoomById(Long roomId);

    void deleteRoomById(Long RoomId);

    RoomDto updateRoomById(Long hotelId, Long roomId, RoomDto roomDto);
}
