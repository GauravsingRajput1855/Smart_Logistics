package com.couriermanagement.dao;

import com.couriermanagement.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentDao extends JpaRepository<Payment, Integer>
{

}
