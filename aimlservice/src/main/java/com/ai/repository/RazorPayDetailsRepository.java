package com.ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ai.entity.RazorPayDetails;

public interface RazorPayDetailsRepository extends JpaRepository<RazorPayDetails, Long> {

	RazorPayDetails findByRazorpayOrderId(String razorpayOrderId);

}
