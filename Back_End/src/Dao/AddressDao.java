package com.couriermanagement.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.couriermanagement.entity.Address;

@Repository
public interface AddressDao extends JpaRepository<Address, Integer> {

}
