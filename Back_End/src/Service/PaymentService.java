package com.couriermanagement.service;

import com.razorpay.RazorpayException;
import org.json.JSONObject;

public interface PaymentService
{
    JSONObject createOrder(int amount) throws RazorpayException;
    boolean verifyPayment(String orderId, String paymentId, String signature) throws RazorpayException;

    boolean verifyPayment(String paymentId);

}

