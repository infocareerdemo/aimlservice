package com.ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ai.entity.RazorpayPayment;

public interface RazorpayPaymentRepository extends JpaRepository<RazorpayPayment, Long> {
	
}
