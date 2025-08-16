package com.project.UrbanNest.controller;

import com.project.UrbanNest.dto.InventoryDto;
import com.project.UrbanNest.dto.UpdateInventoryRequestDto;
import com.project.UrbanNest.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/rooms/{roomId}")
    @Operation(summary = "Get all inventory of a room", tags = {"Admin Inventory"})
    public ResponseEntity<List<InventoryDto>> getAllInventoryByRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(inventoryService.getAllInventoryByRoom(roomId));
    }

    @PatchMapping("/rooms/{roomId}")
    @Operation(summary = "Get all inventory of a room", tags = {"Admin Inventory"})
    public ResponseEntity<Void> updateInventory(@PathVariable Long roomId,
                                                @RequestBody UpdateInventoryRequestDto updateInventoryRequestDto){
        inventoryService.updateInventory(roomId,updateInventoryRequestDto);
        return ResponseEntity.noContent().build();
    }
}
