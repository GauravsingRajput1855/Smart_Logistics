package com.couriermanagement.dto;

import java.util.ArrayList;
import java.util.List;

import com.couriermanagement.entity.Courier;

import lombok.Data;

@Data
public class CourierResponseDto extends CommonApiResponse {

	private List<Courier> couriers = new ArrayList<>();

}
