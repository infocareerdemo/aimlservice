package com.ai.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ai.entity.OrderDetails;

public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {

	List<OrderDetails> findByOrdersId(Long id);

}
