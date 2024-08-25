package com.couriermanagement.dto;

import org.springframework.beans.BeanUtils;

import com.couriermanagement.entity.Address;
import com.couriermanagement.entity.User;

import lombok.Data;

@Data
public class RegisterUserRequestDto {

	private String firstName;

	private String lastName;

	private String emailId;

	private String password;

	private String phoneNo;

	private String role;

	private String street;

	private String landmark;

	private String city;

	private String pincode;

	private String state;

	private String country;

	private int courierId; // courier id for delivery person

	public static User toUserEntity(RegisterUserRequestDto registerUserRequestDto) {
		User user = new User();
		BeanUtils.copyProperties(registerUserRequestDto, user);
		return user;
	}

	public static Address toAddressEntity(RegisterUserRequestDto registerUserRequestDto) {
		Address address = new Address();
		BeanUtils.copyProperties(registerUserRequestDto, address);
		return address;
	}

}
