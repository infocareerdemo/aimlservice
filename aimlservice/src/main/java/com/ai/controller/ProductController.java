package com.ai.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ai.entity.Products;
import com.ai.exception.ApplicationException;
import com.ai.service.ProductService;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController {

	@Autowired
	private ProductService productService;

	@GetMapping("/items")
	@Transactional
	public ResponseEntity<Object> getAllFoodItems() {
		return new ResponseEntity<Object>(productService.getAllFoodItems(), HttpStatus.OK);
	}

	@PostMapping("/saveItem")
	public ResponseEntity<Object> saveOrUpdateFoodItem(@ModelAttribute Products productsRequest, @RequestPart(required = false) MultipartFile productImg) throws ApplicationException, IOException {
		return new ResponseEntity<Object>(productService.saveOrUpdateFoodItem(productsRequest, productImg), HttpStatus.OK);
	}
	
	@DeleteMapping("/id")
	public ResponseEntity<Object> deleteFoodItemById(@RequestParam Long id) throws ApplicationException {
		return new ResponseEntity<Object>(productService.deleteFoodItemById(id), HttpStatus.OK);
	}
	
	@GetMapping("/id")
	@Transactional
	public ResponseEntity<Object> getFoodItemsById(@RequestParam Long id) {
		return new ResponseEntity<Object>(productService.getFoodItemsById(id), HttpStatus.OK);
	}
	
//	@GetMapping("/byLocation")
//	public ResponseEntity<Object> getAllProductsByLocation(@RequestParam Long id) throws ApplicationException {
//		return new ResponseEntity<Object>(productService.getAllProductsByLocation(id), HttpStatus.OK);
//	}
	
	@PostMapping("/validateImage")
	public ResponseEntity<Object> validateImageFile(@RequestPart MultipartFile image) {
		return new ResponseEntity<>(productService.validateImageFile(image), HttpStatus.OK);
	}
	
	@PostMapping("/activeOrInactive")
	public ResponseEntity<Object> activeOrInactiveProduct(@ModelAttribute Products products) throws ApplicationException {
		return new ResponseEntity<>(productService.activeOrInactiveProduct(products), HttpStatus.OK);
	}
	
	
	// To get All Active Product based on location 
	@GetMapping("/activeProducts")
	public ResponseEntity<List<Products>> getActiveProducts(@RequestParam Long locationId) {
	List<Products> activeProducts = productService.getActiveProducts(locationId);
	        return new ResponseEntity<>(activeProducts, HttpStatus.OK);
	    }
}
