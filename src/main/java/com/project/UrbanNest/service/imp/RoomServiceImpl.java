package com.project.UrbanNest.service.imp;

import com.project.UrbanNest.dto.RoomDto;
import com.project.UrbanNest.entity.Hotel;
import com.project.UrbanNest.entity.Room;
import com.project.UrbanNest.entity.User;
import com.project.UrbanNest.exception.ResourceNotFoundException;
import com.project.UrbanNest.exception.UnAuthorisedException;
import com.project.UrbanNest.repository.HotelRepository;
import com.project.UrbanNest.repository.RoomRepository;
import com.project.UrbanNest.service.InventoryService;
import com.project.UrbanNest.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.project.UrbanNest.util.AppUtils.getCurrentUser;


@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final InventoryService inventoryService;
    private final ModelMapper modelMapper;

    @Override
    public RoomDto createNewRoom(Long hotelId,RoomDto roomDto) {
        log.info("Creating a new room in hotel with ID:{}",hotelId);
        Hotel hotel=getHotelById(hotelId);

        User user=getCurrentUser();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorisedException("This user does not own this hotel with id: "+hotelId);
        }

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

        User user=getCurrentUser();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorisedException("This user does not own this hotel with id: "+hotel.getId());
        }

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

        User user=getCurrentUser();
        if(!user.equals(room.getHotel().getOwner())){
            throw new UnAuthorisedException("This user does not own this hotel with id: "+room.getHotel().getId());
        }
        inventoryService.deleteAllInventories(room)  ;
        roomRepository.delete(room);
    }

    @Override
    @Transactional
    public RoomDto updateRoomById(Long hotelId, Long roomId, RoomDto roomDto) {
        log.info("Updating the room with Id: {}",roomId );
        Hotel hotel=getHotelById(hotelId);
        User user=getCurrentUser();

        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorisedException("This user does not own this hotel with ID: "+ hotelId);
        }

        Room room=getRoomByID(roomId);

        modelMapper.map(roomDto,room);
        room.setId(roomId);

        //TODO: if price or inventory is updated, then update the inventory for this room
        room = roomRepository.save(room);

        return modelMapper.map(room, RoomDto.class);
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
