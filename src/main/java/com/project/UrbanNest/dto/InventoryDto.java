package com.project.UrbanNest.dto;

import com.project.UrbanNest.entity.Hotel;
import com.project.UrbanNest.entity.Room;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class InventoryDto {
    private Long id;
    private LocalDate date;
    private Integer bookedCount;
    private Integer totalCount;
    private Integer reservedCount;
    private BigDecimal surgeFactor;
    private BigDecimal price;  //basePrice * surgeFactor
    private Boolean closed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
