package com.project.UrbanNest.controller;

import com.project.UrbanNest.dto.*;
import com.project.UrbanNest.service.HotelService;
import com.project.UrbanNest.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hotels")
public class HotelBrowserController {

    private final InventoryService inventoryService;
    private final HotelService hotelService;

    @GetMapping("/search")
    @Operation(summary = "Search hotels",tags = {"Browse Hotels"})
    public ResponseEntity<Page<HotelPriceResponseDto>> searchHotel(
            @RequestParam String city,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam Integer roomsCount,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
            ){
        HotelSearchRequest hotelSearchRequest=new HotelSearchRequest(city,startDate,endDate,roomsCount,page,size);
        var pageResult=inventoryService.searchHotel(hotelSearchRequest);
        return ResponseEntity.ok(pageResult);
    }

    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(
            @PathVariable Long hotelId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam Long roomsCount
    ){
        HotelInfoRequestDto hotelInfoRequestDto=new HotelInfoRequestDto(startDate,endDate,roomsCount);
        return ResponseEntity.ok(hotelService.getHotelInfoById(hotelId,hotelInfoRequestDto));
    }

}
