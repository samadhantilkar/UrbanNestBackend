package com.project.UrbanNest.service;

import com.project.UrbanNest.dto.BookingDto;
import com.project.UrbanNest.dto.BookingRequestDto;
import com.project.UrbanNest.dto.GuestDto;
import com.project.UrbanNest.dto.HotelReportDto;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface BookingService {

    BookingDto initializeBooking(BookingRequestDto bookingRequest);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtosList);

    String initiatePayment(Long bookingId);

    void capturePayment(Event event);

    void cancelBooking(Long bookingId) throws StripeException;

    String getBookingStatus(Long bookingId);

    List<BookingDto> getAllBookingsByHotelId(Long hotelId) throws AccessDeniedException;

    HotelReportDto getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate) throws AccessDeniedException;

    List<BookingDto> getMyBookings();
}
