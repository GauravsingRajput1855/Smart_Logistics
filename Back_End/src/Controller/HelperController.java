package com.couriermanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.couriermanagement.resource.HelperResource;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("api/helper")
@CrossOrigin(origins = "http://localhost:3000")
public class HelperController {

	@Autowired
	private HelperResource helperResource;

	@GetMapping("/user/role/fetch/all")
	@Operation(summary = "Api to get all user roles in application")
	public ResponseEntity<List<String>> fetchAllUserRoles() {
		return helperResource.fetchAllUserRoles();
	}

	@GetMapping("/courier/type/fetch/all")
	@Operation(summary = "Api to get all courier types in application")
	public ResponseEntity<List<String>> fetchAllCourierType() {
		return helperResource.fetchAllCourierTypes();
	}

	@GetMapping("/delivery/time/fetch/all")
	@Operation(summary = "Api to get all delivery time in application")
	public ResponseEntity<List<String>> fetchAllDeliveryTimes() {
		return helperResource.fetchAllDeliveryTimes();
	}

	@GetMapping("/delivery/status/fetch/all")
	@Operation(summary = "Api to get all delivery status in application")
	public ResponseEntity<List<String>> fetchAllDeliveryStatus() {
		return helperResource.fetchAllDeliveryStatus();
	}




}
