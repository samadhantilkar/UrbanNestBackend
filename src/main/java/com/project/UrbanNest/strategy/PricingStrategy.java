package com.project.UrbanNest.strategy;

import com.project.UrbanNest.entity.Inventory;

import java.math.BigDecimal;

public interface PricingStrategy {

    BigDecimal calculatePrice(Inventory inventory);

}
