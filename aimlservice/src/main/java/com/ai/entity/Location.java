package com.ai.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "location")
public class Location {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "location_id") // Specify the column name for the primary key
	private Long locationId;
	
	@Column(name = "location_name", nullable = false)
	private String locationName;
	
	@Column(name = "company_name" ,  nullable = false)
	private String companyName;
	
	@Column(name = "qr_code", length = 1000) // Increase the length as needed
	private String qrCode;
	
	@Column(name = "last_updated_by",  nullable = false)
	private Long lastUpdatedBy;
	
	@Column(name = "last_updated_dt" ,  nullable = false)
	private LocalDateTime lastUpdatedDt;
	
	
}
