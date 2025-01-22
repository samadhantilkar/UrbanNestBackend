package com.project.UrbanNest.service;

import com.project.UrbanNest.dto.BookingDto;
import com.project.UrbanNest.dto.BookingRequestDto;
import com.project.UrbanNest.dto.GuestDto;
import com.project.UrbanNest.entity.*;
import com.project.UrbanNest.entity.enums.BookingStatus;
import com.project.UrbanNest.exception.ResourceNotFoundException;
import com.project.UrbanNest.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService{

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final GuestRepository guestRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public BookingDto initializeBooking(BookingRequestDto bookingRequest) {

        log.info("Initialising booking for hotel : {}, room:{}, date {} - {}",bookingRequest.getHotelId(),bookingRequest.getRoomId()
                ,bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate());
        Hotel hotel=getHotelById(bookingRequest.getHotelId());
        Room room=getRoomById(bookingRequest.getRoomId());
        List<Inventory> inventoryList=inventoryRepository.findAndLockAvailableInventory(room.getId(), bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate(), bookingRequest.getRoomsCount());

        long daysCount= ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate())+1;

        if(inventoryList.size() !=  daysCount){
            throw new IllegalStateException("Room is not available anymore");
        }
//        Reserve the room/ update the booked count of inventories
        for(Inventory inventory:inventoryList){
            inventory.setReservedCount(inventory.getReservedCount() + bookingRequest.getRoomsCount());
        }

        inventoryRepository.saveAll(inventoryList);



//        TODO: calculate dynamic amount

//        Create the booking
        Booking booking=Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(getCurrentUser())
                .roomCount(bookingRequest.getRoomsCount())
                .amount(BigDecimal.TEN)
                .build();

        booking=bookingRepository.save(booking);

        return modelMapper.map(booking,BookingDto.class);
    }

    @Override
    @Transactional
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestDtosList) {
        log.info("Adding guest for booking with id:{}",bookingId);
        Booking booking=getBookingById(bookingId);

        if(hasBookingExpired(booking)){
            throw new IllegalStateException("Booking Has Already expired");
        }

        if(!(booking.getBookingStatus().equals(BookingStatus.RESERVED))){
            throw new IllegalStateException("Booking is not under reserved state, cannot add guest");
        }

        for(GuestDto guestDto:guestDtosList){
            Guest guest=modelMapper.map(guestDto,Guest.class);
            guest.setUser(getCurrentUser());
            guest=guestRepository.save(guest);
            booking.getGuests().add(guest);
        }

        booking.setBookingStatus(BookingStatus.GUEST_ADDED);
        booking=bookingRepository.save(booking);

        return modelMapper.map(booking, BookingDto.class);
    }

    private User getCurrentUser(){
        User user=new User();
        user.setId(1L);
        return user;// TODO: Remove Dummy User
    }

    private Boolean hasBookingExpired(Booking booking){
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }

    private Booking getBookingById(Long id){
        return bookingRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Booking not found with ID:"+id));
    }

    private Room getRoomById(Long id){
        return roomRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Room not found with ID:"+id));
    }

    private Hotel getHotelById(Long id){
        return hotelRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Hotel not found with ID:"+id));
    }
}
