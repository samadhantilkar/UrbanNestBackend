package com.project.UrbanNest.controller;

import com.project.UrbanNest.dto.BookingDto;
import com.project.UrbanNest.dto.HotelDto;
import com.project.UrbanNest.dto.HotelReportDto;
import com.project.UrbanNest.service.BookingService;
import com.project.UrbanNest.service.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/hotels")
@RequiredArgsConstructor
@Slf4j
public class HotelController {
    private final HotelService hotelService;
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<HotelDto> createNewHotel(@RequestBody HotelDto hotelDto){
        log.info("Attempting to create a new hotel with name: {}", hotelDto.getName());
        HotelDto hotel=hotelService.createNewHotel(hotelDto);
        return new ResponseEntity<>(hotel, HttpStatus.CREATED);
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long hotelId){
        HotelDto hotelDto=hotelService.getHotelById(hotelId);
        return ResponseEntity.ok(hotelDto);
    }

    @PutMapping("/{hotelId}")
    public ResponseEntity<HotelDto> updateHotelById(@RequestBody HotelDto hotelDto,
                                                    @PathVariable(name = "hotelId") Long id){
        HotelDto hotel= hotelService.updateHotelById(id,hotelDto);
        return  ResponseEntity.ok(hotel);
    }

    @DeleteMapping("/{hotelId}")
    public ResponseEntity<Void> deleteHotelById(@PathVariable(name = "hotelId") Long id){
        hotelService.deleteHotelById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{hotelId}/activate")
    public ResponseEntity<Void> activateHotel(@PathVariable Long hotelId){
         hotelService.activeHotel(hotelId);
         return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<HotelDto>> getAllHotels(){
        return ResponseEntity.ok(hotelService.getAllHotels());
    }

    @GetMapping("/{hotelId}/bookings")
    public ResponseEntity< List<BookingDto>> getAllBookingsByHotelId(@PathVariable Long hotelId) throws AccessDeniedException {
        return ResponseEntity.ok(bookingService.getAllBookingsByHotelId(hotelId));
    }

    @GetMapping("/{hotelId}/report")
    public ResponseEntity<HotelReportDto>  getHotelReport(@PathVariable Long hotelId ,
                                                          @RequestParam(required = false)LocalDate startDate,
                                                          @RequestParam(required = false) LocalDate endDate) throws AccessDeniedException {
        if(startDate==null ) startDate =LocalDate.now().minusMonths(1);
        if(endDate==null) endDate=LocalDate.now();

        return ResponseEntity.ok(bookingService.getHotelReport(hotelId,startDate,endDate));

    }
}