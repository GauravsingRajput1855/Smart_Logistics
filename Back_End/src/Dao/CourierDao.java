package com.couriermanagement.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.couriermanagement.entity.Courier;
import com.couriermanagement.entity.User;

@Repository
public interface CourierDao extends JpaRepository<Courier, Integer> {

	Courier findByTrackingNumber(String trackingNumber);

	List<Courier> findBySender(User sender);

	List<Courier> findByDeliveryPerson(User deliveryPerson);

	List<Courier> findByCourier(User courier);

}
