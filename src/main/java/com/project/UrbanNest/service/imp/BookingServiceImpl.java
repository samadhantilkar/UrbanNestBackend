package com.project.UrbanNest.service.imp;

import com.project.UrbanNest.dto.BookingDto;
import com.project.UrbanNest.dto.BookingRequestDto;
import com.project.UrbanNest.dto.GuestDto;
import com.project.UrbanNest.entity.*;
import com.project.UrbanNest.entity.enums.BookingStatus;
import com.project.UrbanNest.exception.ResourceNotFoundException;
import com.project.UrbanNest.exception.UnAuthorisedException;
import com.project.UrbanNest.repository.*;
import com.project.UrbanNest.service.BookingService;
import com.project.UrbanNest.service.CheckoutService;
import com.project.UrbanNest.strategy.PricingService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class
BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final GuestRepository guestRepository;
    private final ModelMapper modelMapper;
    private final CheckoutService checkoutService;
    private final PricingService pricingService;


     @Value("${frontend.url}")
     private String frontendUrl;

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
        inventoryRepository.initBooking(room.getId(), bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate(),bookingRequest.getRoomsCount());


        BigDecimal priceForOneRoom = pricingService.calculateTotalPrice(inventoryList);
        BigDecimal totalPrice=priceForOneRoom.multiply(BigDecimal.valueOf(bookingRequest.getRoomsCount()));

//        Create the booking
        Booking booking=Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(getCurrentUser())
                .roomCount(bookingRequest.getRoomsCount())
                .amount(totalPrice)
                .build();

        booking=bookingRepository.save(booking);

        return modelMapper.map(booking,BookingDto.class);
    }

    @Override
    @Transactional
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestDtosList) {
        log.info("Adding guest for booking with id:{}",bookingId);

        Booking booking=getBookingById(bookingId);

        User user=getCurrentUser();

        if(!user.equals(booking.getUser())) {
            throw new UnAuthorisedException("Booking Does Not Belong to this user with id: "  +user.getId());
        }

        if(hasBookingExpired(booking)){
            throw new IllegalStateException("Booking Has Already expired");
        }

        if(!(booking.getBookingStatus().equals(BookingStatus.RESERVED))){
            throw new IllegalStateException("Booking is not under reserved state, cannot add guest");
        }

        for(GuestDto guestDto:guestDtosList){
            Guest guest=modelMapper.map(guestDto,Guest.class);
            guest.setUser(user);
            guest=guestRepository.save(guest);
            booking.getGuests().add(guest);
        }

        booking.setBookingStatus(BookingStatus.GUEST_ADDED);
        booking=bookingRepository.save(booking);

        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    @Transactional
    public String initiatePayment(Long bookingId) {

        Booking booking=getBookingById(bookingId);
        User user=getCurrentUser();

        if(!user.equals(booking.getUser())){
            throw new UnAuthorisedException("booking does not belong to this user with id :"+user.getId());
        }

        if(hasBookingExpired(booking)){
            throw new IllegalStateException("Booking has already expired");
        }

        String sessionUrl=checkoutService.getCheckoutSession(booking,
                frontendUrl+"/payment/success",frontendUrl+"/payment/failure");

        booking.setBookingStatus(BookingStatus.PAYMENT_PENDING);
        bookingRepository.save(booking);

        return sessionUrl;
    }

    @Override
    @Transactional
    public void capturePayment(Event event) {
        if("checkout.session.completed".equals(event.getType())){
            Session session=(Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if(null != session) return ;

            String sessionId=session.getId();

            Booking booking=bookingRepository
                    .findByPaymentSessionId(sessionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Booking not found with session ID: "+sessionId));

            booking.setBookingStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            inventoryRepository.findAndLockAvailableInventory(booking.getRoom().getId(), booking.getCheckInDate(),
                    booking.getCheckOutDate(), booking.getRoomCount());

            inventoryRepository.confirmBooking(booking.getRoom().getId(), booking.getCheckInDate(),
                    booking.getCheckOutDate(), booking.getRoomCount());

            log.info("Successfully confirmed the booking for booking Id: {}",booking.getId());

        }
        else {
            log.warn("Unhandled event type: {}"+ event.getType());
        }
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId){
        Booking booking=getBookingById(bookingId);
        User user=getCurrentUser();

        if(!user.equals(booking.getUser())){
            throw new UnAuthorisedException("Booking does not belong to this user with id: "+user.getId());
        }

        if(!booking.getBookingStatus().equals(BookingStatus.CONFIRMED)){
            throw new IllegalStateException("Only Confirmed bookings can be cancelled");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        inventoryRepository.findAndLockAvailableInventory(booking.getRoom().getId(), booking.getCheckInDate(),
                booking.getCheckOutDate(), booking.getRoomCount());

        inventoryRepository.cancelBooking(booking.getRoom().getId(), booking.getCheckInDate(),
                booking.getCheckOutDate(), booking.getRoomCount());

        //Handel the refund

        Session session= null;
        try {
            session = Session.retrieve(booking.getPaymentSessionId());
            RefundCreateParams refundParams= RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())
                    .build();

            Refund.create(refundParams);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String  getBookingStatus(Long bookingId) {
        Booking booking=getBookingById(bookingId);
        User user=getCurrentUser();

        if(!user.equals(booking.getUser())){
            throw new UnAuthorisedException("Booking does not belong to this user with id: "+user.getId());
        }

        return booking.getBookingStatus().name();
    }

    private User getCurrentUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private Boolean hasBookingExpired(Booking booking){
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }

    private Booking     getBookingById(Long id){
        return bookingRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Booking not found with ID:"+id));
    }

    private Room getRoomById(Long id){
        return roomRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Room not found with ID:"+id));
    }

    private Hotel getHotelById(Long id){
        return hotelRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Hotel not found with ID:"+id));
    }
}
