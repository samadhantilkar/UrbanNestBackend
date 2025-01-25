package com.project.UrbanNest.controller;


import com.project.UrbanNest.dto.BookingDto;
import com.project.UrbanNest.dto.BookingRequestDto;
import com.project.UrbanNest.dto.GuestDto;
import com.project.UrbanNest.service.BookingService;
import com.stripe.exception.StripeException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
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
    public ResponseEntity<BookingDto> initialiseBooking(@RequestBody BookingRequestDto bookingRequest){
        return ResponseEntity.ok(bookingService.initializeBooking(bookingRequest));
    }

    @PostMapping("/{bookingId}/addGuests")
    public ResponseEntity<BookingDto> addGuest(@PathVariable Long bookingId,
            @RequestBody List<GuestDto> guestDtosList){
        return ResponseEntity.ok(bookingService.addGuests(bookingId,guestDtosList));
    }

    @PostMapping("/{bookingId}/payment")
    public ResponseEntity<Map<String,String>> initiatePayment(@PathVariable Long bookingId){
        String sessionUrl=bookingService.initiatePayment(bookingId);
        return ResponseEntity.ok(Map.of("sessionUrl",sessionUrl));
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId) throws StripeException {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{bookingId}/status")
    public ResponseEntity<Map<String, String>> getBookingStatus(@PathVariable Long bookingId){
        return ResponseEntity.ok(Map.of("status",bookingService.getBookingStatus(bookingId)));
    }

}
