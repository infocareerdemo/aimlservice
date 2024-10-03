package com.ai.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ai.entity.Orders;

public interface OrderRepository extends JpaRepository<Orders, Long> {
//
//	Orders findByOrderId(String oid);
//
	List<Orders> findByUserLoginUserId(Long id);
//
//	List<Orders> findByOrderedDateTimeBetweenAndLocationLocationId(LocalDateTime startOfDay, LocalDateTime endOfDay,
//			Long locationId);
//
	List<Orders> findByOrderedDateTimeBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);

}
