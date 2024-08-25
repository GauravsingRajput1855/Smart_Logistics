package com.couriermanagement.service;

import com.couriermanagement.entity.Address;

public interface AddressService {
	
	Address addAddress(Address address);
	
	Address updateAddress(Address address);
	
	Address getAddressById(int addressId);

}
