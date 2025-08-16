package com.project.UrbanNest.service.imp;

import com.project.UrbanNest.dto.*;
import com.project.UrbanNest.entity.Inventory;
import com.project.UrbanNest.entity.Room;
import com.project.UrbanNest.entity.User;
import com.project.UrbanNest.exception.ResourceNotFoundException;
import com.project.UrbanNest.repository.HotelMinPriceRepository;
import com.project.UrbanNest.repository.InventoryRepository;
import com.project.UrbanNest.repository.RoomRepository;
import com.project.UrbanNest.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.project.UrbanNest.util.AppUtils.getCurrentUser;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;

    @Override
    public void initializeRoomForAYear(Room room) {
        LocalDate today=LocalDate.now();
        LocalDate endDate=today.plusYears(1);
        for(;!today.isAfter(endDate);today=today.plusDays(1)){
            Inventory inventory=Inventory.builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .bookedCount(0)
                    .reservedCount(0)
                    .city(room.getHotel().getCity())
                    .date(today)
                    .price(room.getBasePrice())
                    .surgeFactor(BigDecimal.ONE)
                    .totalCount(room.getTotalCount())
                    .closed(false)
                    .build();
            inventoryRepository.save(inventory);
        }

    }

    @Override
    public void deleteAllInventories(Room room) {
        log.info("Deleting the inventories of room with ID:{}",room.getId());
        inventoryRepository.deleteByRoom(room);
    }

//    @Transactional
    @Override
    public Page<HotelPriceResponseDto> searchHotel(HotelSearchRequest hotelSearchRequest) {
        log.info("Searching Hotel for {} City from, {} to {} ", hotelSearchRequest.getCity(), hotelSearchRequest.getStartDate(),hotelSearchRequest.getEndDate());

        Pageable pageable= PageRequest.of(hotelSearchRequest.getPage(), hotelSearchRequest.getSize());

        Long dateCount=
                ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate(),hotelSearchRequest.getEndDate())+1;

            // business logic - 90days
        Page<HotelPriceDto> hotelPage=
                hotelMinPriceRepository.findHotelsWithAvailableInventory(hotelSearchRequest.getCity()
                , hotelSearchRequest.getStartDate(),  hotelSearchRequest.getEndDate(), hotelSearchRequest.getRoomsCount(),
                dateCount, pageable);

        return hotelPage.map(hotelPriceDto -> {
            HotelPriceResponseDto hotelPriceResponseDto=modelMapper.map(hotelPriceDto.getHotel(),HotelPriceResponseDto.class);
            hotelPriceResponseDto.setPrice(hotelPriceDto.getPrice());
            return hotelPriceResponseDto;
        });
    }

    @Override
    public List<InventoryDto> getAllInventoryByRoom(Long roomId) {

        log.info("Getting all inventory by room for room with Id: {}",roomId);
        Room room = getRoomByID(roomId);
        User user=getCurrentUser();

        if(user.equals(room.getHotel().getRooms())) throw new AccessDeniedException("You are not the owner of room with Id: "+roomId);

        return inventoryRepository.findByRoomOrderByDate(room).stream()
                .map(inventory ->
                        modelMapper.map(inventory, InventoryDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateInventory(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto) {
       log.info("Updating All inventory by room for room with Id: {} between date range: {} - {} ",roomId,
               updateInventoryRequestDto.getStartDate(),updateInventoryRequestDto.getEndDate());

        Room room=getRoomByID(roomId);
        User user=getCurrentUser();

        if(!user.equals(room.getHotel().getOwner())) throw new AccessDeniedException("You are not the owner of room with Id: "+roomId);

        inventoryRepository.getInventoryAndLockBeforeUpdate(roomId,updateInventoryRequestDto.getStartDate(),updateInventoryRequestDto.getEndDate());

        inventoryRepository.updateInventory(roomId,updateInventoryRequestDto.getStartDate()
                ,updateInventoryRequestDto.getEndDate(),updateInventoryRequestDto.getClosed()
                ,updateInventoryRequestDto.getSurgeFactor());

    }

    private Room getRoomByID(Long roomId){
        return roomRepository
                .findById(roomId)
                .orElseThrow(()-> new ResourceNotFoundException("Room Not Found With ID:"+roomId));
    }
}
