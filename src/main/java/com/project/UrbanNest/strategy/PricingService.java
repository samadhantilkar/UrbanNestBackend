package com.project.UrbanNest.strategy;

import com.project.UrbanNest.entity.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PricingService {

    public BigDecimal calculateDynamicPricing(Inventory inventory){

        PricingStrategy pricingStrategy=new BasePricingStrategy();

        //Apply the additional strategies

        pricingStrategy=new SurgePricingStrategy(pricingStrategy);
        pricingStrategy=new OccupancyPricingStrategy(pricingStrategy);
        pricingStrategy=new UrgencyPricingStrategy(pricingStrategy);
        pricingStrategy=new HolidayPricingStrategy(pricingStrategy);

        return pricingStrategy.calculatePrice(inventory);
    }

    //Return the sum of price of this inventory list
    public BigDecimal calculateTotalPrice(List<Inventory> inventoryList){
        return inventoryList.stream()
                .map(inventory -> calculateDynamicPricing(inventory))
                .reduce(BigDecimal.ZERO , BigDecimal::add);

    }
}
