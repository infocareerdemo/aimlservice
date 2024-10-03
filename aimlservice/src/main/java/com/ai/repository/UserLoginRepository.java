package com.ai.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ai.entity.UserLogin;

public interface UserLoginRepository extends JpaRepository<UserLogin, Long>{

	UserLogin findByEmailId(String emailId);
	
	//Optional<UserLogin> findByPhone(Long phone);

	
	UserLogin findByPhone(Long phone);
	
	Optional<UserLogin> findById(Long userId);
	
}
