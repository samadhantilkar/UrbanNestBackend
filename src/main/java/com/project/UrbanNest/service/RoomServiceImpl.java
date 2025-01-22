package com.project.UrbanNest.service;

import com.project.UrbanNest.dto.RoomDto;
import com.project.UrbanNest.entity.Hotel;
import com.project.UrbanNest.entity.Room;
import com.project.UrbanNest.exception.ResourceNotFoundException;
import com.project.UrbanNest.repository.HotelRepository;
import com.project.UrbanNest.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService{

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final InventoryService inventoryService;
    private final ModelMapper modelMapper;

    @Override
    public RoomDto createNewRoom(Long hotelId,RoomDto roomDto) {
        log.info("Creating a new room in hotel with ID:{}",hotelId);
        Hotel hotel=getHotelById(hotelId);
        Room room=modelMapper.map(roomDto, Room.class);
        room.setHotel(hotel);
        room= roomRepository.save(room);

        if(hotel.getActive()){
            inventoryService.initializeRoomForAYear(room);
        }

        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    public List<RoomDto> getAllRoomInHotel(Long hotelId) {
        log.info("Getting all rooms in hotel with ID: {}",hotelId);
        Hotel hotel=getHotelById(hotelId);
        return hotel.getRooms()
                .stream()
                .map((element) -> modelMapper.map(element, RoomDto.class))
                .collect(Collectors.toList() );
    }

    @Override
    public RoomDto getRoomById(Long roomId) {
        log.info("Getting the room With ID: {}",roomId);
        Room room=getRoomByID(roomId);
        return modelMapper.map(room,RoomDto.class);
    }

    @Override
    @Transactional 
    public void deleteRoomById(Long roomId) {
        log.info("Deleting the room with ID: {}", roomId);
        Room room=getRoomByID(roomId);
        inventoryService.deleteAllInventories(room)  ;
        roomRepository.delete(room);
    }

    private Hotel getHotelById(Long Id){
        log.info("Getting the hotel with ID:{}",Id);
        return hotelRepository.findById(Id)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel not found with ID:"+ Id));
    }

    private Room getRoomByID(Long roomId){
        return roomRepository
                .findById(roomId)
                .orElseThrow(()-> new ResourceNotFoundException("Room Not Found With ID:"+roomId));
    }
}
