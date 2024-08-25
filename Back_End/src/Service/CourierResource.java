package com.couriermanagement.resource;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import com.couriermanagement.dao.CourierDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.couriermanagement.dto.CommonApiResponse;
import com.couriermanagement.dto.CourierRequestDto;
import com.couriermanagement.dto.CourierResponseDto;
import com.couriermanagement.entity.Address;
import com.couriermanagement.entity.Courier;
import com.couriermanagement.entity.User;
import com.couriermanagement.exception.CourierSaveFailedException;
import com.couriermanagement.service.AddressService;
import com.couriermanagement.service.CourierService;
import com.couriermanagement.service.UserService;
import com.couriermanagement.utility.Constants.ActiveStatus;
import com.couriermanagement.utility.Constants.DeliveryStatus;
import com.couriermanagement.utility.Helper;

@Component
public class CourierResource {

	private final Logger LOG = LoggerFactory.getLogger(CourierResource.class);

	@Autowired
	private CourierService courierService;

	@Autowired
	private UserService userService;
	@Autowired
	private CourierDao courierRepository;
	
	@Autowired
	private AddressService addressService;

	public ResponseEntity<CommonApiResponse> addCourier(CourierRequestDto request) {

		LOG.info("request received for adding Courier");

		CommonApiResponse response = new CommonApiResponse();

		if (request == null || request.getCourierUserId() == 0 || request.getCustomerRefId() == null) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Address address = CourierRequestDto.toAddressEntity(request); // receiver address

		if (address == null) {
			throw new CourierSaveFailedException("Failed to save Courier Detail, Excpetion Occurred!!!");
		}

		User courierUser = this.userService.getUserById(request.getCourierUserId());

		if (courierUser == null) {
			throw new CourierSaveFailedException("Failed to save Courier Detail, Courier User not found!!!");
		}

		User customer = this.userService.getUserByCustomerIdAndStatus(request.getCustomerRefId(),
				ActiveStatus.ACTIVE.value());

		if (customer == null) {
			throw new CourierSaveFailedException("Failed to save Courier Detail, Active Customer not found!!!");
		}
		
		Address receiverAddress = this.addressService.addAddress(address);

		if (receiverAddress == null) {
			throw new CourierSaveFailedException("Failed to save Courier Detail, Excpetion Occurred!!!");
		}
		
		Courier courier = new Courier();
		courier.setCourierName(request.getCourierName());
		courier.setCourierType(request.getCourierType());
		courier.setReceiverAddress(receiverAddress);
		courier.setCourierDate(
				String.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
		courier.setCourier(courierUser);
		courier.setSender(customer);
		courier.setDeliveryStatus(DeliveryStatus.PENDING.value());
		courier.setTrackingNumber(Helper.getAlphaNumericUniqueId(10));
		courier.setWeight(request.getWeight());
		courier.setReceiverName(request.getReceiverName());

		Courier addedCourier = this.courierService.addCourier(courier);

		if (addedCourier == null) {
			throw new CourierSaveFailedException("Failed to save Courier Detail, Internal Error!!!");
		}

		response.setResponseMessage("Courier Added Successful..!!!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);

	}

	public ResponseEntity<CommonApiResponse> assignDeliveryToCourier(CourierRequestDto request) {

		LOG.info("request received for assigning delivery person for Courier");

		CommonApiResponse response = new CommonApiResponse();

		if (request == null || request.getId() == 0 || request.getDeliveryPersonId() == 0) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Courier courier = this.courierService.getCourierById(request.getId());

		if (courier == null) {
			throw new CourierSaveFailedException("Failed to assign Delivery Person, Courier not found!!!");
		}

		User deliveryPerson = this.userService.getUserById(request.getDeliveryPersonId());

		if (deliveryPerson == null) {
			throw new CourierSaveFailedException("Failed to assign Delivery Person, Delivery Person not found!!!");
		}

		courier.setDeliveryPerson(deliveryPerson);
		courier.setDeliveryStatus(DeliveryStatus.PROCESSING.value());

		Courier addedCourier = this.courierService.updateCourier(courier);

		if (addedCourier == null) {
			throw new CourierSaveFailedException("Failed to assign Delivery Person, Internal Error!!!");
		}

		response.setResponseMessage("Delivery Person Assigned for Courier Successful..!!!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);

	}

	public ResponseEntity<CommonApiResponse> updateCourierDeliveryStatus(CourierRequestDto request) {

		LOG.info("request received for updating courier delivery status");

		CommonApiResponse response = new CommonApiResponse();

		if (request == null || request.getId() == 0 || request.getDeliveryStatus() == null
				|| request.getDeliveryDate() == null || request.getDeliveryTime() == null) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Courier courier = this.courierService.getCourierById(request.getId());

		if (courier == null) {
			throw new CourierSaveFailedException("Failed to assign Delivery Person, Courier not found!!!");
		}

		courier.setMessage(request.getMessage());
		courier.setDeliveryStatus(request.getDeliveryStatus());
		courier.setDeliveryDate(request.getDeliveryDate());
		courier.setDeliveryTime(request.getDeliveryTime());

		Courier addedCourier = this.courierService.updateCourier(courier);

		if (addedCourier == null) {
			throw new CourierSaveFailedException("Failed to update Delivery status, Internal Error!!!");
		}

		response.setResponseMessage("Courier Delivery Status updated Successful..!!!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);

	}

	public ResponseEntity<CourierResponseDto> fetchCourierByCustomer(int customerId) {

		LOG.info("request received for fetching Couriers by Courier");

		CourierResponseDto response = new CourierResponseDto();

		if (customerId == 0) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<CourierResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		User customer = this.userService.getUserById(customerId);

		if (customer == null) {
			response.setResponseMessage("Customet not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<CourierResponseDto>(response, HttpStatus.BAD_REQUEST);

		}

		List<Courier> couriers = this.courierService.getCourierBySender(customer);
		
		if(CollectionUtils.isEmpty(couriers)) {
			response.setResponseMessage("No Couriers found!!!");
			response.setSuccess(false);

			return new ResponseEntity<CourierResponseDto>(response, HttpStatus.BAD_REQUEST);
		}
		
		response.setCouriers(couriers);
		response.setResponseMessage("Couriers fetched Successful..!!!");
		response.setSuccess(true);

		return new ResponseEntity<CourierResponseDto>(response, HttpStatus.OK);

	}

	public ResponseEntity<CourierResponseDto> fetchCourierByCourier(int courierId) {

		LOG.info("request received for fetching Couriers by Courier");

		CourierResponseDto response = new CourierResponseDto();

		if (courierId == 0) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<CourierResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		User courier = this.userService.getUserById(courierId);

		if (courier == null) {
			response.setResponseMessage("Customet not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<CourierResponseDto>(response, HttpStatus.BAD_REQUEST);

		}

		List<Courier> couriers = this.courierService.getCourierByCourier(courier);
		
		if(CollectionUtils.isEmpty(couriers)) {
			response.setResponseMessage("No Couriers found!!!");
			response.setSuccess(false);

			return new ResponseEntity<CourierResponseDto>(response, HttpStatus.BAD_REQUEST);
		}
		
		response.setCouriers(couriers);
		response.setResponseMessage("Couriers fetched Successful..!!!");
		response.setSuccess(true);

		return new ResponseEntity<CourierResponseDto>(response, HttpStatus.OK);

	}

	public ResponseEntity<CourierResponseDto> fetchCourierByDelivery(int deliveryPersonId) {

		LOG.info("request received for fetching Couriers by Delivery Person");

		CourierResponseDto response = new CourierResponseDto();

		if (deliveryPersonId == 0) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<CourierResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		User deliveryPerson = this.userService.getUserById(deliveryPersonId);

		if (deliveryPerson == null) {
			response.setResponseMessage("Delivery Person not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<CourierResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		List<Courier> couriers = this.courierService.getCourierByDeliveryPerson(deliveryPerson);
		
		if(CollectionUtils.isEmpty(couriers)) {
			response.setResponseMessage("No Couriers found!!!");
			response.setSuccess(false);

			return new ResponseEntity<CourierResponseDto>(response, HttpStatus.BAD_REQUEST);
		}
		
		response.setCouriers(couriers);
		response.setResponseMessage("Couriers fetched Successful..!!!");
		response.setSuccess(true);

		return new ResponseEntity<CourierResponseDto>(response, HttpStatus.OK);

	}

	public ResponseEntity<CourierResponseDto> fetchCourierById(int courierId) {

		LOG.info("request received for fetching Couriers by Delivery Person");

		CourierResponseDto response = new CourierResponseDto();

		if (courierId == 0) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<CourierResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		Courier courier = this.courierService.getCourierById(courierId);

		if(courier == null) {
			response.setResponseMessage("Courier Not Found!!!");
			response.setSuccess(false);
			return new ResponseEntity<CourierResponseDto>(response, HttpStatus.BAD_REQUEST);
		}
		
		response.setCouriers(Arrays.asList(courier));
		response.setResponseMessage("Couriers fetched Successful..!!!");
		response.setSuccess(true);

		return new ResponseEntity<CourierResponseDto>(response, HttpStatus.OK);
	}



	public ResponseEntity<CourierResponseDto> fetchAllCouriers() {

		LOG.info("request received for fetching all couriers");

		CourierResponseDto response = new CourierResponseDto();

		// Retrieve all couriers
		List<Courier> couriers = this.courierService.getAllCourier();

		if (CollectionUtils.isEmpty(couriers)) {
			response.setResponseMessage("No Couriers found!!!");
			response.setSuccess(false);
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}

		response.setCouriers(couriers);
		response.setResponseMessage("Couriers fetched successfully..!!!");
		response.setSuccess(true);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	//write code for get courier by role





}
