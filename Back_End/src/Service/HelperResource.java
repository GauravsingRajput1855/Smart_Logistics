package com.couriermanagement.resource;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.couriermanagement.utility.Constants.Category;
import com.couriermanagement.utility.Constants.DeliveryStatus;
import com.couriermanagement.utility.Constants.DeliveryTime;
import com.couriermanagement.utility.Constants.UserRole;

@Component
public class HelperResource {

	public ResponseEntity<List<String>> fetchAllUserRoles() {
		List<String> response = new ArrayList<>();

		for (UserRole role : UserRole.values()) {
			response.add(role.value());
		}

		return new ResponseEntity<List<String>>(response, HttpStatus.OK);
	}

	public ResponseEntity<List<String>> fetchAllCourierTypes() {
		List<String> response = new ArrayList<>();

		for (Category category : Category.values()) {
			response.add(category.value());
		}

		return new ResponseEntity<List<String>>(response, HttpStatus.OK);
	}

	public ResponseEntity<List<String>> fetchAllDeliveryTimes() {
		List<String> response = new ArrayList<>();

		for (DeliveryTime time : DeliveryTime.values()) {
			response.add(time.value());
		}

		return new ResponseEntity<List<String>>(response, HttpStatus.OK);
	}

	public ResponseEntity<List<String>> fetchAllDeliveryStatus() {
		List<String> response = new ArrayList<>();

		for (DeliveryStatus status : DeliveryStatus.values()) {
			response.add(status.value());
		}

		return new ResponseEntity<List<String>>(response, HttpStatus.OK);
	}

	

}
