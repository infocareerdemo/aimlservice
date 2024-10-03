package com.ai.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ai.entity.Products;

public interface ProductsRepository extends JpaRepository<Products, Long> {

	
//	Products findByProductName(String name);
//
//	List<Products> findByProductActive(boolean b);
//
	Products findByProductNameAndLocationLocationId(String productName, Long locId);
//
	Products findByProductNameAndLocationLocationIdAndProductIdNotIn(String productName, Long locId, List<Long> id);
//
	List<Products> findByLocationLocationId(Long id);
//
//	List<Products> findByProductActiveTrue();
//
	List<Products> findByLocationLocationIdAndProductActive(Long locationId, boolean b);

}
