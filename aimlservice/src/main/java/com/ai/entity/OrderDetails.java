package com.ai.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Entity
@Table(name = "order_details") // Specify the table name
public class OrderDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_details_id") // Specify the column name for the primary key
	private Long orderDetailsId;
	
	// order_id of order_details  reference to id column of orders table 
	// many to one  - always refere the FK to PK
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "order_conf_id", referencedColumnName = "id", nullable = false) // Lakkaiya - Changed reference column from order_id to id
	private Orders orders;
	
	// food_items_id of order_details table refere to id column of foodItems
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ordered_product_id",referencedColumnName = "product_id", nullable = false)
	private Products products;
	
	
	@Column(name = "quantity", nullable = false)
	private Long quantity;
	
	@Positive(message = "Unit Price must be positive")
	@Column(name = "unit_price", nullable = false)
	private double unitPrice;
	
	//@Positive(message = "Tax amount Price must be positive")
	@Column(name = "tax_amount")
	private double taxAmount;
	
	@Positive(message = "Total Price must be positive")
	@Column(name = "total_price", nullable = false)
	private double totalPrice;
	
	@Column(name = "order_date_time", nullable = false)
	private LocalDateTime orderDateTime;
}
