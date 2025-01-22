package com.project.UrbanNest.entity;

import com.project.UrbanNest.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class Payment extends BaseEntity{

    @Column(unique = true,nullable = false)
    private String transactionId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(nullable = false,precision = 10,scale = 2)
    private BigDecimal amount;

    @OneToOne(fetch = FetchType.LAZY)
    private Booking booking;
}
