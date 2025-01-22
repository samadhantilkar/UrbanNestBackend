package com.project.UrbanNest.controller;

import com.project.UrbanNest.dto.HotelDto;
import com.project.UrbanNest.dto.HotelInfoDto;
import com.project.UrbanNest.dto.HotelPriceDto;
import com.project.UrbanNest.dto.HotelSearchRequest;
import com.project.UrbanNest.service.HotelService;
import com.project.UrbanNest.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hotels")
public class HotelBrowserController {

    private final InventoryService inventoryService;
    private final HotelService hotelService;

    @GetMapping("/search")
    public ResponseEntity<Page<HotelPriceDto>> searchHotel(@RequestBody HotelSearchRequest hotelSearchRequest){
        var page=inventoryService.searchHotel(hotelSearchRequest);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId){
        return ResponseEntity.ok(hotelService.getHotelInfoById(hotelId));
    }

}
