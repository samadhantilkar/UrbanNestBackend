package com.project.UrbanNest.controller;


import com.project.UrbanNest.dto.*;
import com.project.UrbanNest.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class HotelBookingController {

    private final BookingService bookingService;

    @PostMapping("/init")
    @Operation(summary = "Initiate the Booking",tags = {"Booking Flow"})
    public ResponseEntity<BookingDto> initialiseBooking(@RequestBody BookingRequestDto bookingRequest){
        return ResponseEntity.ok(bookingService.initializeBooking(bookingRequest));
    }

    @PostMapping("/{bookingId}/addGuests")
    @Operation(summary = "Add guest Ids to the booking",tags = {"Booking Flow"})
    public ResponseEntity<BookingDto> addGuest(@PathVariable Long bookingId,
            @RequestBody List<GuestDto> guestDtosList){
        return ResponseEntity.ok(bookingService.addGuests(bookingId,guestDtosList));
    }

    @PostMapping("/{bookingId}/payment")
    @Operation(summary = "Initiate payments flow for the booking", tags = {"Booking Flow"})
    public ResponseEntity<BookingPaymentInitResponseDto> initiatePayment(@PathVariable Long bookingId){
        String sessionUrl=bookingService.initiatePayment(bookingId);
        return ResponseEntity.ok(new BookingPaymentInitResponseDto(sessionUrl));
    }

    @PostMapping("/{bookingId}/cancel")
    @Operation(summary = "Cancel the booking", tags = {"Booking Flow"})
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.noContent().build();
    }

    //front end polling this every 3/4 second to check if this booking is successful or not
   @GetMapping("/{bookingId}/status")
   @Operation(summary = "Check the status of the booking", tags = {"Booking Flow"})
    public ResponseEntity<Map<String, String>> getBookingStatus(@PathVariable Long bookingId){
        return ResponseEntity.ok(Map.of("status",bookingService.getBookingStatus(bookingId)));
    }

    @GetMapping("/{bookingId}")
    @Operation(summary = "Get the booking by Id", tags = {"Booking Flow"})
    public ResponseEntity<BookingDto> getBookingById(@PathVariable Long bookingId){
        return ResponseEntity.ok(bookingService.getBookingById(bookingId));
    }

}
