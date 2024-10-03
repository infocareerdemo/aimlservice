package com.ai.dto;

import java.util.List;

import com.ai.entity.OrderDetails;
import com.ai.entity.Orders;

import lombok.Data;

@Data
public class OrderSummary {

	public com.ai.entity.Orders orders;
	
	public List<OrderDetails> orderDetails;
	
	// private OrderTime orderTime;   // New field for OrderTime entity
	// private String errorMessage;
}
