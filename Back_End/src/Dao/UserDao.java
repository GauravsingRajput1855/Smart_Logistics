package com.couriermanagement.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.couriermanagement.entity.User;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {

	User findByEmailId(String email);

	User findByEmailIdAndStatus(String email, String status);

	User findByRoleAndStatusIn(String role, List<String> status);

	List<User> findByRole(String role);

	List<User> findByCourierAndRole(User courier, String role);

	List<User> findByCourierAndRoleAndStatusIn(User courier, String role, List<String> status);

	User findByEmailIdAndRoleAndStatus(String emailId, String role, String status);

	List<User> findByRoleAndStatus(String role, String status);

	User findByCustomerRefIdAndStatus(String customerRefId, String status);

	//List<User> findByRole(String role);

}
