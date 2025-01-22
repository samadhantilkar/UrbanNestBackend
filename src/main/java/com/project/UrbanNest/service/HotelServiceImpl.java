package com.project.UrbanNest.service;

import com.project.UrbanNest.dto.HotelDto;
import com.project.UrbanNest.dto.HotelInfoDto;
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
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;
    private final RoomRepository roomRepository;

    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {
        log.info("Creating a new hotel with name: {}",hotelDto.getName());
        Hotel hotel =modelMapper.map(hotelDto, Hotel.class);
        hotel.setActive(false);
        Hotel savedHotel=hotelRepository.save(hotel);
        log.info("Created a new hotel with ID: {}",savedHotel.getId());
        return modelMapper.map(hotel,HotelDto.class);
    }

    @Override
    public HotelDto findHotelById(Long Id) {
        log.info("Getting the hotel with ID: {}",Id);
        Hotel hotel=hotelRepository.findById(Id)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel not found with ID: "+Id));
        return modelMapper.map(hotel,HotelDto.class);
    }

    @Override
    public HotelDto updateHotelById(Long id, HotelDto hotelDto) {
        log.info("Updating the hotel with ID: {}", id);
        Hotel hotel=hotelRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel not found with ID: "+id));
        modelMapper.map(hotelDto,hotel);
        hotel.setId(id);
        hotel=hotelRepository.save(hotel);
        return modelMapper.map(hotel,HotelDto.class);
    }

    @Override
    @Transactional
    public void deleteHotelById(Long id) {
        Hotel hotel=getHotelById(id);
        for(Room room:hotel.getRooms()){
            inventoryService.deleteAllInventories(room);
            roomRepository.deleteById(room.getId());
        }
        hotelRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void activeHotel(Long hotelId) {
        log.info("Activating the hotel with ID: {}",hotelId);
        Hotel hotel=getHotelById(hotelId);
        hotel.setActive(true);
        log.info("Saving The hotel With Activation");
        hotelRepository.save(hotel);

//        Assuming only do it once
        for(Room room:hotel.getRooms()){
            inventoryService.initializeRoomForAYear(room);
        }

    }

    @Override
    public HotelInfoDto getHotelInfoById(Long hotelId) {
        Hotel hotel=getHotelById(hotelId);
        List<RoomDto> rooms= hotel.getRooms()
                .stream()
                .map((element) -> modelMapper.map(element, RoomDto.class))
                .toList();
        return new HotelInfoDto(modelMapper.map(hotel,HotelDto.class),rooms);
    }

    private Hotel getHotelById(Long id){
        return hotelRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Hotel not found with ID="+id));
    }

}
