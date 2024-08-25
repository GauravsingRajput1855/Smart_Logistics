package com.couriermanagement.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.couriermanagement.dto.CommonApiResponse;
import com.couriermanagement.dto.RegisterUserRequestDto;
import com.couriermanagement.dto.UserLoginRequest;
import com.couriermanagement.dto.UserLoginResponse;
import com.couriermanagement.dto.UserResponseDto;
import com.couriermanagement.dto.UserStatusUpdateRequestDto;
import com.couriermanagement.entity.Address;
import com.couriermanagement.entity.User;
import com.couriermanagement.exception.UserNotFoundException;
import com.couriermanagement.exception.UserSaveFailedException;
import com.couriermanagement.service.AddressService;
import com.couriermanagement.service.UserService;
import com.couriermanagement.utility.Constants.ActiveStatus;
import com.couriermanagement.utility.Constants.UserRole;
import com.couriermanagement.utility.Helper;
import com.couriermanagement.utility.JwtUtils;

import jakarta.transaction.Transactional;

@Component
@Transactional
public class UserResource {

	private final Logger LOG = LoggerFactory.getLogger(UserResource.class);

	@Autowired
	private UserService userService;

	@Autowired
	private AddressService addressService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtils jwtUtils;

	public ResponseEntity<CommonApiResponse> registerAdmin(RegisterUserRequestDto registerRequest) {

		LOG.info("Request received for Register Admin");

		CommonApiResponse response = new CommonApiResponse();

		if (registerRequest == null) {
			response.setResponseMessage("user is null");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (registerRequest.getEmailId() == null || registerRequest.getPassword() == null) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User existingUser = this.userService.getUserByEmailAndStatus(registerRequest.getEmailId(),
				ActiveStatus.ACTIVE.value());

		if (existingUser != null) {
			response.setResponseMessage("User already register with this Email");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User user = RegisterUserRequestDto.toUserEntity(registerRequest);

		user.setRole(UserRole.ROLE_ADMIN.value());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		user.setStatus(ActiveStatus.ACTIVE.value());

		existingUser = this.userService.addUser(user);

		if (existingUser == null) {
			response.setResponseMessage("failed to register admin");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		response.setResponseMessage("Admin registered Successfully");
		response.setSuccess(true);

		LOG.info("Response Sent!!!");

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> registerUser(RegisterUserRequestDto request) {

		LOG.info("Received request for register user");

		CommonApiResponse response = new CommonApiResponse();

		if (request == null) {
			response.setResponseMessage("user is null");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User existingUser = this.userService.getUserByEmailAndStatus(request.getEmailId(), ActiveStatus.ACTIVE.value());

		if (existingUser != null) {
			response.setResponseMessage("User with this Email Id already resgistered!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (request.getRole() == null) {
			response.setResponseMessage("bad request ,Role is missing");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User user = RegisterUserRequestDto.toUserEntity(request);
		
		if (user.getRole().equals(UserRole.ROLE_CUSTOMER.value())) {
			user.setCustomerRefId(Helper.getAlphaNumericUniqueId(5));
		} else if (user.getRole().equals(UserRole.ROLE_COURIER.value())) {
			user.setCourierRefId(Helper.getAlphaNumericUniqueId(5));
		}

		String encodedPassword = passwordEncoder.encode(user.getPassword());

		user.setStatus(ActiveStatus.ACTIVE.value());
		user.setPassword(encodedPassword);

		// delivery person is for Courier, so we need to set Courier
		if (user.getRole().equals(UserRole.ROLE_DELIVERY.value())) {

			User courier = this.userService.getUserById(request.getCourierId());

			if (courier == null) {
				throw new UserNotFoundException("Courier Not found");
			}

			user.setCourier(courier);

		}

		Address address = RegisterUserRequestDto.toAddressEntity(request);

		Address savedAddress = this.addressService.addAddress(address);

		if (savedAddress == null) {
			throw new UserSaveFailedException("Registration Failed, Exception Occurred");
		}

		user.setAddress(savedAddress);
		existingUser = this.userService.addUser(user);

		if (existingUser == null) {
			throw new UserSaveFailedException("Registration Failed, Exception Occurred");
		}
		response.setResponseMessage("User registered Successfully");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<UserLoginResponse> login(UserLoginRequest loginRequest) {

		LOG.info("Received request for User Login");

		UserLoginResponse response = new UserLoginResponse();

		if (loginRequest == null) {
			response.setResponseMessage("Missing Input");
			response.setSuccess(false);

			return new ResponseEntity<UserLoginResponse>(response, HttpStatus.BAD_REQUEST);
		}

		String jwtToken = null;
		User user = null;

		List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority(loginRequest.getRole()));

		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmailId(),
					loginRequest.getPassword(), authorities));
		} catch (Exception ex) {
			response.setResponseMessage("Invalid email or password.");
			response.setSuccess(false);
			return new ResponseEntity<UserLoginResponse>(response, HttpStatus.BAD_REQUEST);
		}

		jwtToken = jwtUtils.generateToken(loginRequest.getEmailId());

		user = this.userService.getUserByEmailIdAndRoleAndStatus(loginRequest.getEmailId(), loginRequest.getRole(),
				ActiveStatus.ACTIVE.value());

		// user is authenticated
		if (jwtToken != null) {
			response.setUser(user);
			response.setResponseMessage("Logged in sucessful");
			response.setSuccess(true);
			response.setJwtToken(jwtToken);
			return new ResponseEntity<UserLoginResponse>(response, HttpStatus.OK);
		}

		else {
			response.setResponseMessage("Failed to login");
			response.setSuccess(false);
			return new ResponseEntity<UserLoginResponse>(response, HttpStatus.BAD_REQUEST);
		}

	}

	public ResponseEntity<UserResponseDto> getUsersByRole(String role) {

		UserResponseDto response = new UserResponseDto();

		if (role == null) {
			response.setResponseMessage("missing role");
			response.setSuccess(false);
			return new ResponseEntity<UserResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		List<User> users = new ArrayList<>();

		users = this.userService.getUserByRoleAndStatus(role, ActiveStatus.ACTIVE.value());

		if (users.isEmpty()) {
			response.setResponseMessage("No Users Found");
			response.setSuccess(false);
		}

		List<User> userDtos = new ArrayList<>();

		for (User user : users) {
			userDtos.add(user);
		}

		response.setUsers(userDtos);
		response.setResponseMessage("User Fetched Successfully");
		response.setSuccess(true);

		return new ResponseEntity<UserResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> updateUserStatus(UserStatusUpdateRequestDto request) {

		LOG.info("Received request for updating the user status");

		CommonApiResponse response = new CommonApiResponse();

		if (request == null) {
			response.setResponseMessage("bad request, missing data");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (request.getUserId() == 0 || request.getStatus() == null) {
			response.setResponseMessage("bad request, missing request data");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}
		

		User user = null;
		user = this.userService.getUserById(request.getUserId());

		user.setStatus(request.getStatus());

		User updatedUser = this.userService.updateUser(user);

		if (updatedUser == null) {
			throw new UserSaveFailedException("Failed to update the User status");
		}

		response.setResponseMessage("User " + request.getStatus() + " Successfully!!!");
		response.setSuccess(true);
		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);

	}

	public ResponseEntity<UserResponseDto> getDeliveryPersonsByCourier(int courierId) {

		UserResponseDto response = new UserResponseDto();

		if (courierId == 0) {
			response.setResponseMessage("missing courier id");
			response.setSuccess(false);
			return new ResponseEntity<UserResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		User courier = this.userService.getUserById(courierId);

		if (courier == null) {
			throw new UserNotFoundException("Courier Not Found in DB");
		}

		List<User> users = new ArrayList<>();

		users = this.userService.getUserByCourierAndRoleAndStatusIn(courier, UserRole.ROLE_DELIVERY.value(),
				Arrays.asList(ActiveStatus.ACTIVE.value()));

		if (users.isEmpty()) {
			response.setResponseMessage("No Delivery Guys Found");
			response.setSuccess(false);
		}

		response.setUsers(users);
		response.setResponseMessage("User Fetched Successfully");
		response.setSuccess(true);

		return new ResponseEntity<UserResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> deleteCourier(int courierId) {

		UserResponseDto response = new UserResponseDto();

		if (courierId == 0) {
			response.setResponseMessage("missing courier id");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User courier = this.userService.getUserById(courierId);

		if (courier == null) {
			response.setResponseMessage("Courier not found");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		List<User> deliveryPersons = new ArrayList<>();

		deliveryPersons = this.userService.getUserByCourierAndRoleAndStatusIn(courier, UserRole.ROLE_DELIVERY.value(),
				Arrays.asList(ActiveStatus.ACTIVE.value()));

		courier.setStatus(ActiveStatus.DEACTIVATED.value());
		User deletedCourier = this.userService.updateUser(courier);

		// deactivating the courier
		if (deletedCourier == null) {
			throw new UserSaveFailedException("Failed to deactivate the courier!!!");
		}

		// deactivating the all couriers delivery persons
		if (!deliveryPersons.isEmpty()) {

			for (User deliveryPerson : deliveryPersons) {
				deliveryPerson.setStatus(ActiveStatus.DEACTIVATED.value());
			}

			List<User> deletedDeliveryPerons = this.userService.updateAllUser(deliveryPersons);

			if (CollectionUtils.isEmpty(deletedDeliveryPerons)) {
				throw new UserSaveFailedException("Failed to deactivate the courier!!!");
			}

		}

		response.setResponseMessage("Courier Deactivated Successful");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);

	}

	public ResponseEntity<CommonApiResponse> deleteDeliveryPerson(int deliveryId) {

		UserResponseDto response = new UserResponseDto();

		if (deliveryId == 0) {
			response.setResponseMessage("missing delivery person id");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User delivery = this.userService.getUserById(deliveryId);

		if (delivery == null) {
			response.setResponseMessage("Delivery Person not found");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		delivery.setStatus(ActiveStatus.DEACTIVATED.value());

		User deletedDelivery = this.userService.updateUser(delivery);

		// deactivating the seller
		if (deletedDelivery == null) {
			throw new UserSaveFailedException("Failed to deactivate the delivery person!!!");
		}

		response.setResponseMessage("Delivery Person Deactivated Successful");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);

	}

	public ResponseEntity<UserResponseDto> getUserByUserId(int userId) {

		UserResponseDto response = new UserResponseDto();

		if (userId == 0) {
			response.setResponseMessage("missing courier id");
			response.setSuccess(false);
			return new ResponseEntity<UserResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		User user = this.userService.getUserById(userId);

		if (user == null) {
			throw new UserNotFoundException("User Not Found in DB");
		}

		response.setUsers(Arrays.asList(user));
		response.setResponseMessage("User Fetched Successfully");
		response.setSuccess(true);

		return new ResponseEntity<UserResponseDto>(response, HttpStatus.OK);
	}

}
