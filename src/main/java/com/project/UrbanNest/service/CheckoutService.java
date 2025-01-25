package com.project.UrbanNest.service;

import com.project.UrbanNest.entity.Booking;

public interface CheckoutService {

    String getCheckoutSession(Booking booking, String successUrl, String failureUrl);
}
