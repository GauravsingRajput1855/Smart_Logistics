package com.couriermanagement.controller;

import com.couriermanagement.entity.User;
import com.couriermanagement.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.couriermanagement.dto.CommonApiResponse;
import com.couriermanagement.dto.RegisterUserRequestDto;
import com.couriermanagement.dto.UserLoginRequest;
import com.couriermanagement.dto.UserLoginResponse;
import com.couriermanagement.dto.UserResponseDto;
import com.couriermanagement.dto.UserStatusUpdateRequestDto;
import com.couriermanagement.resource.UserResource;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

@RestController
@RequestMapping("api/user")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

	@Autowired
	private UserResource userResource;

	@Autowired
	private UserServiceImpl service;

	// RegisterUserRequestDto, we will set only email, password & role from UI
	@PostMapping("/admin/register")
	@Operation(summary = "Api to register Admin")
	public ResponseEntity<CommonApiResponse> registerAdmin(@RequestBody RegisterUserRequestDto request) {
		return userResource.registerAdmin(request);
	}

	// for customer and restaurant register
	@PostMapping("register")
	@Operation(summary = "Api to register customer or restaurant user")
	public ResponseEntity<CommonApiResponse> registerUser(@RequestBody RegisterUserRequestDto request) {
		return this.userResource.registerUser(request);
	}
	
	@PostMapping("login")
	@Operation(summary =  "Api to login any User")
	public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest userLoginRequest) {
		return userResource.login(userLoginRequest);
	}
	
	@GetMapping("/fetch")
	@Operation(summary =  "Api to get User by user id")
	public ResponseEntity<UserResponseDto> fetchUser(@RequestParam("userId") int userId) {
		return userResource.getUserByUserId(userId);
	}
	
	@GetMapping("/fetch/role-wise")
	@Operation(summary =  "Api to get Users By Role")
	public ResponseEntity<UserResponseDto> fetchAllUsersByRole(@RequestParam("role") String role) throws JsonProcessingException {
		return userResource.getUsersByRole(role);
	}
	
	@GetMapping("/fetch/courier/delivery-person")
	@Operation(summary =  "Api to get Delivery persons by courier")
	public ResponseEntity<UserResponseDto> fetchDeliveryPerson(@RequestParam("courierId") int courierId) {
		return userResource.getDeliveryPersonsByCourier(courierId);
	}
	
	@PutMapping("update/status")
	@Operation(summary =  "Api to update the user status")
	public ResponseEntity<CommonApiResponse> updateUserStatus(@RequestBody UserStatusUpdateRequestDto request) {
		return userResource.updateUserStatus(request);
	}
	
	@DeleteMapping("delete/courier")
	@Operation(summary =  "Api to delete the Courier")
	public ResponseEntity<CommonApiResponse> deleteRestaurant(@RequestParam("courierId") int courierId) {
		return userResource.deleteCourier(courierId);
	}
	
	@DeleteMapping("delete/courier/delivery-person")
	@Operation(summary =  "Api to delete the Courier Delivery Person")
	public ResponseEntity<CommonApiResponse> deleteDeliveryPerson(@RequestParam("deliveryId") int deliveryId) {
		return userResource.deleteDeliveryPerson(deliveryId);
	}

	@GetMapping("/fetch/couriers")
	public ResponseEntity<List<User>> getCouriers() {
		List<User> users = service.getCouriers();
		if (users.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(users);
	}

}
