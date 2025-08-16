package com.project.UrbanNest.service;

import com.project.UrbanNest.dto.BookingDto;
import com.project.UrbanNest.dto.BookingRequestDto;
import com.project.UrbanNest.dto.GuestDto;
import com.project.UrbanNest.dto.HotelReportDto;
import com.stripe.model.Event;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {

    BookingDto initializeBooking(BookingRequestDto bookingRequest);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtosList);

    String initiatePayment(Long bookingId);

    void capturePayment(Event event);

    void cancelBooking(Long bookingId);

    BookingDto getBookingById(Long bookingId);

    String getBookingStatus(Long bookingId);

    List<BookingDto> getAllBookingsByHotelId(Long hotelId);

    HotelReportDto getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate);

    List<BookingDto> getMyBookings();
}
