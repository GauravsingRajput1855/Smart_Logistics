package com.couriermanagement.dto;

import java.util.ArrayList;
import java.util.List;

import com.couriermanagement.entity.User;

import lombok.Data;

@Data
public class UserResponseDto extends CommonApiResponse {
	
	private List<User> users = new ArrayList<>();

}
