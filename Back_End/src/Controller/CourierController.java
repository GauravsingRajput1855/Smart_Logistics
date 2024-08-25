package com.couriermanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.couriermanagement.dto.CommonApiResponse;
import com.couriermanagement.dto.CourierRequestDto;
import com.couriermanagement.dto.CourierResponseDto;
import com.couriermanagement.resource.CourierResource;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("api/courier")
@CrossOrigin(origins = "http://localhost:3000")
public class CourierController {
	
	@Autowired
	private CourierResource courierResource;

//	@Autowired
//	private PaymentServiceImpl paymentService;

	@PostMapping("add")
	@Operation(summary = "Api to add the courier")
	public ResponseEntity<CommonApiResponse> addCourier(@RequestBody CourierRequestDto request) {
		// Check if payment is successful
	//	boolean isPaymentSuccessful = paymentService.verifyPayment(paymentId);

		//if (isPaymentSuccessful) {
			return this.courierResource.addCourier(request);
	//	} else {
		//	return ResponseEntity.status(400).body(new CommonApiResponse("Payment verification failed", false));
		//}
	}
	
	@PutMapping("assign-delivery")
	@Operation(summary = "Api to assign the delivery person to deliver courier")
	public ResponseEntity<CommonApiResponse> assignDeliveryToCourier(@RequestBody CourierRequestDto request) {
		return this.courierResource.assignDeliveryToCourier(request);
	}
	
	@PutMapping("update-delivery-status")
	@Operation(summary = "Api to update the courier delivery status")
	public ResponseEntity<CommonApiResponse> updateCourierDeliveryStatus(@RequestBody CourierRequestDto request) {
		return this.courierResource.updateCourierDeliveryStatus(request);
	}
	
	@GetMapping("fetch/customer-wise")
	@Operation(summary = "Api to get the couriers by customer")
	public ResponseEntity<CourierResponseDto> fetchCourierByCustomer(@RequestParam("customerId") int customerId) {
		return this.courierResource.fetchCourierByCustomer(customerId);
	}
	
	@GetMapping("fetch/courier-wise")
	@Operation(summary = "Api to get the couriers by courier")
	public ResponseEntity<CourierResponseDto> fetchCourierByCourier(@RequestParam("courierId") int courierId) {
		return this.courierResource.fetchCourierByCourier(courierId);
	}
	
	@GetMapping("fetch/delivery-wise")
	@Operation(summary = "Api to get the couriers by delivery person")
	public ResponseEntity<CourierResponseDto> fetchCourierByDelivery(@RequestParam("deliveryPersonId") int deliveryPersonId) {
		return this.courierResource.fetchCourierByDelivery(deliveryPersonId);
	}
	
	@GetMapping("fetch")
	@Operation(summary = "Api to get the courier by courier id")
	public ResponseEntity<CourierResponseDto> fetchCourierById(@RequestParam("courierId") int courierId) {
		return this.courierResource.fetchCourierById(courierId);
	}

//	@GetMapping("/fetch/all")
//	@Operation(summary = "Api to get all couriers in the application")
//	public ResponseEntity<CourierResponseDto> fetchAllCouriers() {
//		return courierResource.fetchAllCouriers();
//	}

	@GetMapping("/fetch/all")
	@Operation(summary = "Api to get all middle admin in the application")
	public ResponseEntity<CourierResponseDto> fetchAllCouriers() {
		return courierResource.fetchAllCouriers();
	}
	
}
