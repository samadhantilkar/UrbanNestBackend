package com.project.UrbanNest.strategy;

import com.project.UrbanNest.entity.Inventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
public class UrgencyPricingStrategy implements PricingStrategy
{

    private final PricingStrategy wrapped;


    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price=wrapped.calculatePrice(inventory);

        LocalDate today=LocalDate.now();

        if(!inventory.getDate().isBefore(today)  && inventory.getDate().isBefore(today.plusDays(7))){
            price=price.multiply(BigDecimal.valueOf(1.15));
        }

        return price;

    }
}
