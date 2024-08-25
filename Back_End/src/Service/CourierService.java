package com.couriermanagement.service;

import java.util.List;

import com.couriermanagement.entity.Courier;
import com.couriermanagement.entity.User;

public interface CourierService {
	
	Courier addCourier(Courier courier);
	
	Courier updateCourier(Courier courier);
	
	Courier getCourierById(int courierId);
	
	List<Courier> getAllCourier();
	
	Courier getCourierByTrackingNumber(String trackingNumber);
	
	List<Courier> getCourierBySender(User sender);
	
	List<Courier> getCourierByCourier(User courier);
	
	List<Courier> getCourierByDeliveryPerson(User deliveryPerson);
	
}
