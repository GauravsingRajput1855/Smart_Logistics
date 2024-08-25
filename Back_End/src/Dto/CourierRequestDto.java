package com.couriermanagement.dto;

import org.springframework.beans.BeanUtils;

import com.couriermanagement.entity.Address;

import lombok.Data;

@Data
public class CourierRequestDto {
	
	// for assigning delivery person for Courier Package
	private int id;  // for assigning delivery person
	
	private int deliveryPersonId;  // for assigning delivery person
	
	// for updating the Courier Status by Delivery Person
	private String deliveryStatus;

	private String deliveryDate;

	private String deliveryTime; // evening, afternoon....

	private String message; // General message related to the courier, updated by Delivery Person

	// for adding courier
	private String courierName;

	private String customerRefId;
	
	private int courierUserId;  // User primary key
	
    private String street;
	
	private String landmark;

	private String city;

	private String pincode;
	
	private String state;
	
	private String country;

	private String receiverName; // Name of the receiver

	private String courierType; // Type of the courier (e.g., luggage, document, gift, etc.)

	private double weight; // Weight of the courier package	
	
	public static Address toAddressEntity(CourierRequestDto courierRequestDto) {
		Address address = new Address();
		BeanUtils.copyProperties(courierRequestDto, address);
		return address;
	}

}
