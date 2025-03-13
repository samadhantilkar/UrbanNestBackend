package com.project.UrbanNest.service.imp;

import com.project.UrbanNest.dto.HotelDto;
import com.project.UrbanNest.dto.HotelInfoDto;
import com.project.UrbanNest.dto.RoomDto;
import com.project.UrbanNest.entity.Hotel;
import com.project.UrbanNest.entity.Room;
import com.project.UrbanNest.entity.User;
import com.project.UrbanNest.exception.ResourceNotFoundException;
import com.project.UrbanNest.exception.UnAuthorisedException;
import com.project.UrbanNest.repository.HotelRepository;
import com.project.UrbanNest.repository.RoomRepository;
import com.project.UrbanNest.service.HotelService;
import com.project.UrbanNest.service.InventoryService;
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

        User user= getCurrentUser();
        hotel.setOwner(user);

        Hotel savedHotel=hotelRepository.save(hotel);
        log.info("Created a new hotel with ID: {}",savedHotel.getId());
        return modelMapper.map(hotel,HotelDto.class);
    }

    @Override
    public HotelDto getHotelById(Long Id) {
        log.info("Getting the hotel with ID: {}",Id);
        Hotel hotel=hotelRepository.findById(Id)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel not found with ID: "+Id));

        User user=getCurrentUser();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorisedException("This user does not own this hotel with id: "+user.getId());
        }

        return modelMapper.map(hotel,HotelDto.class);
    }

    @Override
    public HotelDto updateHotelById(Long id, HotelDto hotelDto) {
        log.info("Updating the hotel with ID: {}", id);
        Hotel hotel=hotelRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel not found with ID: "+id));

        User user=getCurrentUser();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorisedException("This user does not own this hotel with id: "+user.getId());
        }


        modelMapper.map(hotelDto,hotel);
        hotel.setId(id);
        hotel=hotelRepository.save(hotel);
        return modelMapper.map(hotel,HotelDto.class);
    }

    @Override
    @Transactional
    public void deleteHotelById(Long id) {
        Hotel hotel=findHotelById(id);

        User user=getCurrentUser();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorisedException("This user does not own this hotel with id: "+user.getId());
        }

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
        Hotel hotel=findHotelById(hotelId);

        User user=getCurrentUser();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorisedException("This user does not own this hotel with id: "+user.getId());
        }

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
        Hotel hotel=findHotelById(hotelId);
        List<RoomDto> rooms= hotel.getRooms()
                .stream()
                .map((element) -> modelMapper.map(element, RoomDto.class))
                .toList();
        return new HotelInfoDto(modelMapper.map(hotel,HotelDto.class),rooms);
    }

    @Override
    public List<HotelDto> getAllHotels() {
        User user=getCurrentUser();

        log.info("Getting all hotels for the admin user with Id: {}",user.getId());

        List<Hotel> hotels=hotelRepository.findByOwner(user);
        return hotels.stream()
                .map(hotel -> modelMapper.map(hotel,HotelDto.class))
                .collect(Collectors.toList());
    }

    private Hotel findHotelById(Long id){
        return hotelRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Hotel not found with ID="+id));
    }



}
