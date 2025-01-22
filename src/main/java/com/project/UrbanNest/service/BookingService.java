package com.project.UrbanNest.service;

import com.project.UrbanNest.dto.BookingDto;
import com.project.UrbanNest.dto.BookingRequestDto;
import com.project.UrbanNest.dto.GuestDto;

import java.util.List;

public interface BookingService {

    BookingDto initializeBooking(BookingRequestDto bookingRequest);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtosList);
}
