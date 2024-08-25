package com.couriermanagement.dto;

import lombok.Data;

@Data
public class PaymentDto
{
    private String orderId;
    private String paymentId;
    private String signature;
    private int amount;
}
